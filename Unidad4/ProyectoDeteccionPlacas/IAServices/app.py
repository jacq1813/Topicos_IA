
from flask import Flask, request, jsonify
# Importamos gc (Garbage Collector) para limpiar memoria a la fuerza
import gc 
import cv2
import numpy as np
import torch

app = Flask(__name__)

print("🚀 Servidor Iniciado (Modo Ahorro de Memoria)")

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

    texto_placa = "NO DETECTADO"
    confianza_max = 0.0

    try:
        # --- PASO 1: Cargar YOLO y Detectar ---
        print("🧠 Cargando YOLO...")
        from ultralytics import YOLO
        model = YOLO('best.pt') 
        
        results = model(frame)
        
        # Guardamos los recortes de las placas detectadas
        recortes = []
        for r in results:
            boxes = r.boxes
            for box in boxes:
                conf = float(box.conf[0])
                if conf > 0.4:
                    x1, y1, x2, y2 = map(int, box.xyxy[0])
                    crop = frame[y1:y2, x1:x2]
                    recortes.append((crop, conf))

        # --- LIMPIEZA CRÍTICA ---
        # Borramos YOLO de la memoria RAM inmediatamente
        del model
        del results
        del YOLO
        gc.collect() # Forzamos al basurero a limpiar la RAM
        torch.cuda.empty_cache() if torch.cuda.is_available() else None
        print("🗑️ Memoria liberada de YOLO")

        # --- PASO 2: Cargar EasyOCR solo si hay placas ---
        if recortes:
            print("📖 Cargando EasyOCR...")
            import easyocr
            # Cargamos EasyOCR SOLO ahora que hay espacio
            reader = easyocr.Reader(['es'], gpu=False) 
            
            for crop, conf in recortes:
                ocr_result = reader.readtext(crop)
                for (bbox, text, prob) in ocr_result:
                    if prob > 0.3:
                        limpio = text.replace(" ", "").upper()
                        if conf > confianza_max:
                            confianza_max = conf
                            texto_placa = limpio
            
            # Limpiamos EasyOCR también
            del reader
            del easyocr
            gc.collect()

        return jsonify({
            'placa': texto_placa,
            'confianza': confianza_max
        }), 200

    except Exception as e:

        print("\n\n" + "="*30)
        print(f"❌ ERROR FATAL OCURRIDO:")
        print(f"TIPO: {type(e)}")
        print(f"MENSAJE: {str(e)}")
        print("="*30 + "\n\n")
        
        # En caso de error, intentar limpiar memoria
        gc.collect()
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=10000)