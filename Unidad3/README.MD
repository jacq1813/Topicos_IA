# Optimización de Riego con Algoritmo de Enjambre de Partículas (PSO) - Guasave, Sinaloa

Este proyecto implementa el algoritmo de **Optimización por Enjambre de Partículas (PSO)** para resolver un problema de **optimización de localización** de alta dimensión aplicado a la agricultura de precisión. El objetivo es determinar la ubicación óptima de una red de sensores de humedad en campos agrícolas de Guasave, Sinaloa, maximizando la representatividad de la variabilidad hídrica del terreno.

---

## Descripción del Problema
El desafío es encontrar la **distribución ideal de 100 sensores de humedad** en la zona agrícola de Guasave, considerando factores críticos que afectan el riego:

1.  **Topografía:** Micro variaciones de elevación (10 a 50 metros).
2.  **Cultivos:** Diferentes requerimientos hídricos de **Maíz**, **Tomate** y **Chile**.
3.  **Condiciones del Suelo:** Zonas con alta salinidad y baja materia orgánica.

##
El problema se modela en un **espacio de búsqueda multidimensional** (200 variables) para lograr la máxima cobertura de estas variabilidades.

---

## Algoritmo PSO
El algoritmo PSO fue elegido por su **eficacia en problemas de optimización continua y multidimensional** y su capacidad para encontrar un buen balance entre **exploración** y **explotación**.

### 1. Modelado de la Partícula (Solución)
* **Partícula:** Representa la configuración completa de la **red de 100 sensores**.
    * **Dimensión:** 2 x 100 = 200 dimensiones.
    * **Vector de Posición (X_i):** (Lat_1, Lon_1, Lat_2, Lon_2,..., Lat_100, Lon_100).

### 2. Función de Aptitud (Fitness Function)
La función se diseñó para **maximizar** el puntaje, premiando los sensores ubicados en áreas de alta importancia y alta variabilidad, mientras penaliza la redundancia.

#### 2.1 Puntaje x Sensor:
* F(X) = Σ k=1 hasta N (Puntaje_Sensork) x Penalizacion_distancia
* Puntaje dependiendo del peso:
    * Peso: (Peso_cultivo x 0.4) + (∆Humedad x 0.3) + (Salinidad x 0.2) + (∆Elevacion x 0.1)
    * ∆Humedad : Diferencia entre Humedad Actual y Optima x Cultivo
    * ∆Elevacion: Diferencia entre Elevacion Actual y Optima x Cultivo
    * Optimas por cultivo: (Maiz %30, Tomate %40, Chile %25)
---
* **Ponderación de Variables:** Un punto de dato de cultivo es más importante si su humedad se desvía del óptimo, o si tiene alta salinidad o elevación atípica.
* **Restricción de Redundancia:** Se aplica una penalización (x 0.3) si dos sensores están a menos de 0.05 km (50 metros) de distancia, forzando la dispersión y la no aglomeración.
* **Calidad de cobertura:** Se premia la proximidad (distancia <= 0.5 km) del sensor a los puntos de datos de alto peso, e incluye un bonus por el número total de puntos cubiertos.

---
#### 2.2 Calculo de Distancia Harversine (Puntos geograficos)
#### Considerando esto se tomó la fórmula de Haversine para determinar la distancia entre 2 puntos (sensores), esto ya que es útil para distancias cortas como lo es la zona de estudio.

* d=2r∙arcsin⁡(√(〖sin〗^2 (∆l/2)+cos(l_1 )∙cos(l_2 ) 〖∙sin〗^2 (∆λ/2) )) 
Donde 
- d= distancia entre los dos puntos
- r= radio de la tierra (aproximadamente 6371 km)
- φ_1,φ_2= latitudes de los puntos (en radianes)
- λ_1,λ_2= longitudes de los puntos (en radianes)
- Δφ=φ_2-φ_1
- Δλ=λ_2-λ_1



---
### 3. Ecuaciones de Movimiento (Base Original de PSO)
La posición y velocidad de cada partícula se actualizan en cada iteración:

* **Actualización de Velocidad:**
    v_i (t+1)=ωv_i (t)+ c_1 r_1  (P_(〖best〗_i ) (t)- P_i (t))+ c_2  r_2  (P_global (t)-P_i (t))
    * P_best, i: Mejor posición personal histórica.
    * G_best: Mejor posición global del enjambre.

* **Actualización de Posición:**
    X_i (t+1)= X_i  (t)+V_i (t+1)

Donde:
- V_i (t+1)=Nueva velocidad
- V_i (t)=Velocidad actual
- ω= (Inercia)  Mantiene la dirección de la particula
- c_1=Componente cognitivo,regula el comp.exploratorio del algoritmo 
- c_2=Componente social,movimiento hacia la mejor pos.encontrada
- r_1 r_2=Factores de aleatoriedad(num alea entre 0 y 1)
- X_i (t)=Posicion actual de la Particula
---

## 📈 Resultados y Análisis de Eficiencia

Se realizaron 6 compilaciones para calibrar los parámetros y asegurar una convergencia óptima.

### Resumen de Pruebas y Parámetros
| N. Prueba | Partículas | Iteraciones máx. |ω (Inercia) | C_1 | C_2 | **Mejor Aptitud** | It. de Convergencia |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | **15** | 30 | **0.7** | **1.5** | **1.5** | **232**  | 28  |
| **2** | 30 | 30 | 0.7 | 1.5 | 1.5 | **232**  | 27  |
| 4 (Control) | 15 | 30 | 1.0 | 2.0 | 2.0 | 152  | 4  |
| 5 | 25 | 100 | 1.0 | 2.0 | 2.0 | 195  | 16  |

### Hallazgos Clave

* **Calibración Crítica de Inercia:** La inercia de **ω=0.7** fue esencial. La Prueba 4 (con=1.0) tuvo una **convergencia rápida y prematura** a un valor subóptimo de 152, mientras que el valor más bajo de 0.7 permitió una búsqueda **más exhaustiva** y evitó quedar atrapado en mínimos locales.
* **Solución Óptima:** Las pruebas **1 y 2** lograron la **máxima aptitud de 232**. La **Prueba 1 (15 partículas)** fue considerada la **más eficiente**, ya que logró el mismo resultado con menos recursos computacionales.
* **Conclusión:** La distribución final de los 100 sensores cumple el objetivo de **máxima representatividad** al aplicar la función de aptitud ponderada y la restricción de aglomeración.

---

## 💻 Parámetros Esenciales del Código (`PSO.ipynb`)
La clase `PSOAgricola` gestiona la optimización. Estos parámetros se definen en el Notebook:

| Constante/Variable | Valor Típico Usado | Descripción en el Código |
| :--- | :--- | :--- |
| `NUM_SENSORES` | `100` | Número de coordenadas (Lat/Lon) que forman el vector de cada partícula. |
| `HUMEDAD_OPTIMA` | `{'Maíz': 30.0, 'Tomate': 40.0, 'Chile': 25.0}` | Metas de humedad del suelo para cada cultivo, clave para calcular ∆Humedad en el peso del punto. |
| `ELEVACION_PROMEDIO` | `30.0` | Valor de referencia para penalizar la desviación de la elevación y capturar la variabilidad topográfica. |
| `DISTANCIA_MIN_SENSORES` | `0.05` km | Límite inferior de la distancia Haversine. Si la distancia entre dos sensores es menor, se aplica una penalización fuerte (x 0.3) en el *fitness*. |
| `DISTANCIA_MAX_COBERTURA` | `0.5` km | Límite Para cada sensor llegue a un optimo alcanzado. |

### 4. Referencias
- Avitex. (2011, February 6). Haversine formula in Python (bearing and distance between two GPS points). Stack Overflow. https://stackoverflow.com/questions/4913349/haversine-formula-in-python-bearing-and-distance-between-two-gps-points

-	APLICACIÓN ALGORITMO DE ENJAMBRE DE PARTICULAS PARA OPTIMIZACIÓN PATZ EN REDES DE RIEGO. (2023). Easy Hidro. https://www.serea23.com/wp-content/uploads/2023/06/6257.pdf

-	Optimización de Enjambre de Partículas aplicada al problema del Viajero Bi-objetivo. (2006). Inteligencia Artificial. Revista Iberoamericana, 10(32). https://www.redalyc.org/pdf/925/92503209.pdf

-	Yadav, A. (2023, March 30). Distance between two Geo-Locations in Python - AskPython. AskPython. https://www.askpython.com/python/examples/find-distance-between-two-geo-locations


## Autores

- [Peña Lopez Miguel Angel ](https://github.com/KingSplatt)

- [Robles Rios Jacquelin](https://github.com/jacq1813)
