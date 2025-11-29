# IAServices: Microservicio de Inferencia (Backend)

Este directorio aloja el microservicio encargado del procesamiento de inteligencia artificial para la aplicación móvil. Funciona como una API REST containerizada que recibe imágenes desde el cliente Android, procesa la detección de vehículos y matrículas, y retorna la información extraída.

##Descripción Técnica

El servicio está construido sobre **Flask** y encapsulado en **Docker**, diseñado para operar como una caja negra que orquesta dos modelos de IA:

1.  **Detección de Objetos:** Utiliza **YOLOv8** (modelo personalizado `best.pt`) para localizar el vehículo y la placa en la imagen.
2.  **Reconocimiento Óptico (OCR):** Utiliza **EasyOCR** para extraer el texto alfanumérico de la región de la placa recortada.

## Stack Tecnológico

- **Lenguaje:** Python 3.9+
- **Framework Web:** Flask
- **Containerización:** Docker
- **IA / Visión Computacional:**
  - Ultralytics YOLOv8
  - EasyOCR
  - OpenCV (headless)
  - PyTorch

## Estructura del Servicio

- **`Dockerfile`**: Definición de la imagen del contenedor. Incluye la instalación de dependencias del sistema operativo (librerías GL) necesarias para que OpenCV corra en entornos Linux sin interfaz gráfica (como servidores en la nube).
- **`app.py`**: Punto de entrada de la API. Contiene la lógica de recepción de la imagen, preprocesamiento, inferencia y formateo de la respuesta JSON.
- **`best.pt`**: Pesos del modelo YOLOv8n entrenado específicamente para la detección de placas vehiculares.
- **`requirements.txt`**: Dependencias de Python necesarias para el entorno de inferencia.

## Contrato de API (Endpoints)

La aplicación Android debe consumir el siguiente endpoint para procesar las fotografías.

### `POST /analizar`

Recibe una imagen binaria, ejecuta la inferencia y devuelve el texto detectado.

**Parámetros (Body - multipart/form-data):**

| Clave    | Tipo | Descripción                                       |
| :------- | :--- | :------------------------------------------------ |
| `imagen` | File | Archivo de imagen (JPG/PNG) capturado por la app. |

**Respuesta Exitosa (JSON 200 OK):**

```json
{
  "placa": "NHG-96-02",
  "confianza": 0.89
}
```
