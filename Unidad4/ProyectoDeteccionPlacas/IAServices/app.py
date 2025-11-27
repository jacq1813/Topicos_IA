from flask import Flask, request, jsonify
import gc
import cv2
import numpy as np
import torch

app = Flask(__name__)

print("🚀 Servidor Iniciado (Versión Optimizada)")

@app.route('/analizar', methods=['POST'])
def analizar_placa():
    if 'imagen' not in request.files:
        return jsonify({'error': 'Falta la imagen'}), 400

    file = request.files['imagen']
    filestr = file.read()
    npimg = np.frombuffer(filestr, np.uint8)
    frame = cv2.imdecode(npimg, cv2.IMREAD_COLOR)

    if frame is None:
        return jsonify({'error': 'Imagen corrupta'}), 400
    
    # --- OPTIMIZACIÓN REDUCIR TAMAÑO ---
    # Las fotos de celular son enormes. Reducimos a 640px de ancho.
    # Esto reduce el consumo de RAM de 50MB a 1MB y acelera YOLO X10 veces.
    height, width = frame.shape[:2]
    max_size = 640
    if width > max_size or height > max_size:
        scale = max_size / max(width, height)
        new_width = int(width * scale)
        new_height = int(height * scale)
        frame = cv2.resize(frame, (new_width, new_height))
        print(f"📉 Imagen redimensionada a {new_width}x{new_height}")

    texto_placa = "NO DETECTADO"
    confianza_max = 0.0

    try:
        # 1. Cargar YOLO
        print("🧠 Cargando YOLO...")
        from ultralytics import YOLO
        model = YOLO('best.pt') 
        
        results = model(frame)
        
        recortes = []
        for r in results:
            boxes = r.boxes
            for box in boxes:
                conf = float(box.conf[0])
                if conf > 0.4:
                    x1, y1, x2, y2 = map(int, box.xyxy[0])
                    # Asegurar coordenadas dentro de la imagen
                    x1, y1 = max(0, x1), max(0, y1)
                    x2, y2 = min(width, x2), min(height, y2)
                    
                    crop = frame[y1:y2, x1:x2]
                    if crop.size > 0:
                        recortes.append((crop, conf))

        # Limpiar YOLO
        del model
        del results
        del YOLO
        gc.collect()
        print("🗑️ Memoria liberada de YOLO")

        # 2. Cargar EasyOCR (Solo si hay recortes)
        if recortes:
            print("📖 Cargando EasyOCR...")
            import easyocr
            reader = easyocr.Reader(['es'], gpu=False) 
            
            for crop, conf in recortes:
                try:
                    ocr_result = reader.readtext(crop)
                    for (bbox, text, prob) in ocr_result:
                        if prob > 0.3:
                            limpio = text.replace(" ", "").upper()
                            # Filtro básico: Las placas suelen tener más de 3 caracteres
                            if len(limpio) > 3 and conf > confianza_max:
                                confianza_max = conf
                                texto_placa = limpio
                except Exception as e_ocr:
                    print(f"Error leyendo recorte: {e_ocr}")
            
            del reader
            del easyocr
            gc.collect()

        return jsonify({
            'placa': texto_placa,
            'confianza': confianza_max
        }), 200

    except Exception as e:
        gc.collect()
        print(f"❌ ERROR FATAL: {str(e)}")
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=10000)