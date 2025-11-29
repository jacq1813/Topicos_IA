# Sistema integral de detección placas vehiculares para reportes de incidentes

Este repositorio contiene el código fuente completo de una solución _end-to-end_ para el reconocimiento automático de matrículas. El sistema integra una aplicación móvil nativa, microservicios de inteligencia artificial y servicios backend para la gestión de datos.

## Arquitectura del Sistema

El proyecto está dividido en 4 módulos principales que interactúan entre sí. A continuación se describe el propósito de cada directorio principal:

1. App/ (Frontend - Android)
   Rol: Cliente móvil.

Descripción: Aplicación nativa desarrollada en Android Studio. Es la interfaz que utiliza el usuario final para capturar fotografías, visualizar los resultados de la detección en tiempo real y gestionar el historial de reportes.

Comunicación: Consume la API de IAServices para procesar imágenes y WebServices para guardar la información.

2. IAServices/ (Microservicio de IA)
   Rol: Motor de Inferencia.

Descripción: API RESTful containerizada (Docker) que aloja los modelos de Inteligencia Artificial.

\*Tecnologías: Flask, YOLOv8, EasyOCR.

Función: Recibe una imagen desde la App, detecta el vehículo/placa, extrae el texto mediante OCR y devuelve los datos estructurados (JSON).

3. WebServices/ (Backend & Base de Datos)
   Rol: Lógica de negocio y persistencia.

Descripción: Conjunto de APIs encargadas de la gestión de usuarios, almacenamiento de registros de placas detectadas y lógica administrativa para los reportes.

Función: Actúa como la fuente de verdad para el almacenamiento de datos históricos generados por la aplicación móvil.

4. ModeloDetectorPlacas/ (Data Science Lab)
   Rol: Entrenamiento y Validación.

Descripción: Contiene los Jupyter Notebooks y scripts utilizados para la ingeniería de datos.

Función: Aquí se realiza el preprocesamiento del dataset, el entrenamiento del modelo YOLOv8 y las pruebas de validación que generan el archivo de pesos (best.pt) utilizado posteriormente en IAServices.

## Flujo de Trabajo General

**Entrenamiento:** Se utiliza ModeloDetector para crear un modelo de IA capaz de ver placas. El resultado es un archivo .pt.

**Despliegue IA:** Ese archivo .pt se coloca en IAServices, el cual se despliega en la nube (Hugging Face) para estar listo para recibir peticiones.

**Uso:** El usuario abre la App en su teléfono y toma una foto.

**Procesamiento:** La App envía la foto a IAServices, el cual responde con el texto de la placa (ej: "NHG-96-02").

**Almacenamiento:** La App toma ese texto y lo envía a WebServices para guardarlo en la base de datos con fecha y hora.

## Tecnologías Principales

Móvil: Android SDK, Kotlin.

IA/ML: Python, PyTorch, Ultralytics YOLOv8, EasyOCR, OpenCV, HuggingFaces.

Backend: Python, Render.

Nube: Hugging Face Spaces, Render/Azure.

### Desarrollado por:

Jacquelin Robles
Miguel Angel Peña Lopez
