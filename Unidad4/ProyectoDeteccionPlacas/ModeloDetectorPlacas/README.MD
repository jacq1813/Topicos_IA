# Entrenamiento de Modelo de Detección de Placas

Este directorio contiene el flujo de trabajo completo para el **Entrenamiento, Validación y Prueba** de un sistema de detección de matrículas vehiculares. El proyecto abarca desde la ingeniería de datos (normalización de etiquetas) hasta la generación de inferencias visuales utilizando **YOLOv8** y **EasyOCR**.

## Estructura del Proyecto

El entorno de trabajo debe estar organizado de la siguiente manera:

```text
├── dataset/                   # Conjunto de datos combinado
│   ├── train/                 # Imágenes para entrenamiento
│   ├── valid/                 # Imágenes para validación durante el entrenamiento
│   └── test/                  # Imágenes para pruebas finales (inéditas para el modelo)
│
├── Prueba2.ipynb              # Notebook principal (Data Eng + Training + Testing)
├── data.yaml                  # Archivo de configuración generado automáticamente
└── runs/                      # Carpeta generada automáticamente con los resultados del entrenamiento
```

> **Nota:** Si bien esta es la estructura de directorios requerida para la ejecución, **no todos los archivos se encuentran en el repositorio** (específicamente las imágenes del dataset) debido a su gran tamaño. Por favor, descarga los datos desde los enlaces proporcionados abajo.

## Origen del dataset

Para garantizar la robustez del modelo, se ha creado un **Dataset Combinado** unificando dos fuentes de alta calidad adquiridas en Roboflow. Las imágenes fueron organizadas en las carpetas `train`, `valid` y `test`.

**Fuentes utilizadas:**

1.  **DETECTOR PLACAS Object Detection Model (v2)**
    - _Autor:_ CHATO (2025)
    - _Enlace:_ [Roboflow Universe - Detector Placas](https://universe.roboflow.com/chato-oegqb/detector-placas-mkuhl/dataset/2)
2.  **Placas Object Detection Dataset**
    - _Autor:_ Xavier Jimenez (2022)
    - _Enlace:_ [Roboflow Universe - Placas Dataset](https://universe.roboflow.com/xavier-jimenez/placas-stcpz)

## Requisitos e Instalación

Este proyecto requiere Python 3.10+. Las dependencias principales se instalan directamente en el notebook, pero se enumeran aquí para referencia:

- **Ultralytics (YOLOv8)**: Para la detección de objetos.
- **EasyOCR**: Para la lectura de texto en las placas detectadas.
- **OpenCV & Matplotlib**: Para el procesamiento y visualización de imágenes.

```bash
pip install ultralytics easyocr opencv-python-headless matplotlib
```

## Flujo de trabajo (Notebook: `Prueba2.ipynb`)

El Jupyter Notebook ejecuta el pipeline completo en 5 fases:

### 1\. Configuración del Entorno

Verificación de GPU/CPU e instalación de librerías necesarias.

### 2\. Ingeniería de Datos (Script de Normalización)

Los datasets originales contienen archivos `_annotations.txt` con coordenadas absolutas. El notebook incluye un script que:

- Recorre las carpetas `train`, `valid` y `test`.
- Lee las imágenes para obtener sus dimensiones reales.
- **Normaliza las coordenadas** al formato YOLO (0 a 1) usando la fórmula:
  $$ x*{centro} = \frac{x*{min} + \frac{w}{2}}{W*{img}}, \quad y*{centro} = \frac{y*{min} + \frac{h}{2}}{H*{img}} $$
- Genera archivos `.txt` individuales para cada imagen, requeridos por YOLO.

### 3\. Entrenamiento (Fine-Tuning)

Se entrena un modelo **YOLOv8 Nano** (`yolov8n.pt`) pre-entrenado:

- **Épocas:** 20
- **Tamaño de imagen:** 640px
- **Optimizador:** Auto (AdamW)

### 4\. Evaluación de Métricas

Al finalizar el entrenamiento, se evalúa el modelo con el set de validación.

- **Resultados obtenidos:**
  - Precision (Exactitud): **\~99.5%**
  - Recall (Sensibilidad): **\~85.0%**
  - mAP50: **\~99.0%**

### 5\. Pruebas de Inferencia (Demo Visual)

Se toman imágenes aleatorias de la carpeta `test` (que el modelo nunca ha visto) para simular un entorno real:

1.  Detección de la placa con el modelo entrenado (`best.pt`).
2.  Recorte automático de la región de interés (ROI).
3.  Lectura del texto usando **EasyOCR**.
4.  Visualización del resultado final (Imagen con Bounding Box + Texto leído).

## Resultado Final

El entrenamiento genera un archivo de pesos optimizado ubicado en:
`runs/detect/placas_modelo/weights/best.pt`

Este archivo es el que se debe exportar para su uso en producción o despliegue en la nube.

---

## Autores

- [@Peña Lopez Miguel Angel](https://github.com/KingSplatt)

- [@Robles Rios Jacquelin](https://github.com/jacq1813)
