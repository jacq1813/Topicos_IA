from flask import Flask, request, jsonify
from ultralytics import YOLO
import easyocr
import cv2
import numpy as np

app = Flask(__name__)

# Cargar modelos en memoria al iniciar
print("⏳ Cargando Cerebro de IA...")
model = YOLO('best.pt') 
reader = easyocr.Reader(['es'], gpu=False)
print("✅ IA Lista para recibir fotos.")

@app.route('/analizar', methods=['POST'])
def analizar_placa():
    if 'imagen' not in request.files:
        return jsonify({'error': 'Falta la imagen'}), 400

    file = request.files['imagen']
    
    # 1. Leer imagen
    filestr = file.read()
    npimg = np.frombuffer(filestr, np.uint8)
    frame = cv2.imdecode(npimg, cv2.IMREAD_COLOR)

    if frame is None:
        return jsonify({'error': 'Imagen corrupta'}), 400

    try:
        # 2. YOLO Detecta
        results = model(frame)
        
        texto_placa = "NO DETECTADO"
        confianza_max = 0.0

        for r in results:
            boxes = r.boxes
            for box in boxes:
                # Extraer confianza
                confianza = float(box.conf[0])
                if confianza > 0.4:
                    # Extraer coordenadas
                    x1, y1, x2, y2 = box.xyxy[0]
                    x1, y1, x2, y2 = int(x1), int(y1), int(x2), int(y2)

                    # 3. Recortar (Crop)
                    placa_recorte = frame[y1:y2, x1:x2]
                    
                    # 4. OCR Lee
                    ocr_result = reader.readtext(placa_recorte)
                    for (bbox, text, prob) in ocr_result:
                        if prob > 0.3:
                            texto_limpio = text.replace(" ", "").upper()
                            # Guardamos el mejor resultado
                            if confianza > confianza_max:
                                confianza_max = confianza
                                texto_placa = texto_limpio

        return jsonify({
            'placa': texto_placa,
            'confianza': confianza_max
        }), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=10000)