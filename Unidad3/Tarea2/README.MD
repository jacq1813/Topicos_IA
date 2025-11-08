# Algoritmo Genético para el Problema del Viajante (TSP)

Este proyecto implementa un Algoritmo Genético (AG) para encontrar una solución aproximada al Problema del Viajante (Traveling Salesperson Problem, TSP). El TSP busca la ruta más corta posible que visita un conjunto de ciudades y regresa a la ciudad de origen.

## Descripción del Algoritmo

El código tsp_ga.py utiliza la siguiente estructura y operadores estándar de Algoritmos Genéticos para optimizar la distancia total de la ruta:


- **Permutación de objetos Ciudad:** Cada individuo (ruta) es una lista ordenada de ciudades.

- **Aptitud (Fitness):** El inverso de la distancia total de la ruta ($1 / Distancia).Esto haciendo convertir el problema de minimización (distancia) a uno de maximización (aptitud).

- **Inicialización:** Creación de una población inicial de rutas aleatorias. Genera diversidad en la generación 0.

- **Selección:** Elitismo (mantiene a los mejores) + Selección por Ruleta (proporcional a la aptitud). Garantiza que las mejores soluciones pasen directamente y que las soluciones prometedoras tengan una oportunidad de cruce.

- **Cruce (Crossover):** Cruce de Orden 1 (Order 1 Crossover, OX1). Un método de cruce diseñado para problemas de ordenamiento (TSP) que garantiza que todas las ciudades se incluyan exactamente una vez en la descendencia.

- **Mutación:** por Intercambio (Swap Mutation). Intercambia la posición de dos ciudades en la ruta con una pequeña probabilidad (razon_mutacion). Esto evita la convergencia prematura.

## Cómo ejecutar el código

- **Dependencias:** Asegúrate de tener Python instalado. Necesitarás las siguientes librerías:

- **pip install numpy pandas**


Ejecución de la Demostración:
El archivo a nivel main debes tener una lista con datos de ejemplo (10 ciudades) y parámetros preestablecidos Y Simplemente ejecuta el archivo

## Selección de parametros y sus valores

- **tamano_poblacion:** valor seleccionado: **100**, (rango idoneo: 50-500); Un tamaño moderado (100) equilibra la diversidad necesaria para un problema NP-Hard y la velocidad de ejecución por generación.

- **generaciones:** valor seleccionado: **100** (rango idoneo: 50-5000): Valor base para demostración y pruebas iniciales. Permite observar la convergencia. Para soluciones definitivas se requieren >= 500 generaciones.

- **indiv_seleccionados:** valor_seleccionado: **30** (rango idoneo: 1% - 30%): Un porcentaje alto (30%) garantiza una fuerte explotación de los mejores resultados, forzando una convergencia rápida y evitando la pérdida de la mejor ruta.

- **razon_mutacion:** valor seleccionado: **0.01** : Una tasa de mutación muy baja es vital. Una tasa alta convertiría el AG en una búsqueda aleatoria. El 1% asegura la exploración para escapar de mínimos locales.

## Pruebas unitarias del Algoritmo

Se realizaron pruebas de componentes clave para validar que la lógica interna del AG funcione correctamente:

### 1. Prueba de Aptitud (Fitness)

Objetivo: Verificar que la función distanciaRuta calcula la distancia total del ciclo correctamente.

Escenario: Ruta simple de 3 ciudades que forma un triángulo rectángulo (Distancia esperada: 12.0).

Resultado: Distancia calculada: 12.0. Validado.

### 2. Prueba de Cruce (OX1)

Objetivo: Asegurar que la función reproduccion (Cruce OX1) produce un hijo válido (una permutación completa).

Escenario: Cruce entre dos rutas de 5 ciudades.

Resultado: El hijo generado mantuvo la longitud correcta (5) y fue un contenido válido (sin duplicados/faltantes). Validado.

Importancia: Confirma que el operador OX1 no "contamina" la población con rutas imposibles.

### 3. Prueba de Mutación (Swap)

Objetivo: Validar que la función mutacion introduce cambios válidos.

Escenario: Mutación forzada (100% de probabilidad) sobre una ruta de 5 ciudades.

Resultado: El individuo mutado mantuvo la longitud y contenido válido, verificando que solo se modificó el orden (intercambio) de las ciudades. Validado.

### 4. Prueba de Selección (Ruleta)

Objetivo: Verificar que el mecanismo de seleccionRutas favorece a los individuos con mayor aptitud.

Escenario: Población falsa con un individuo súper apto (Aptitud: 100.0) y el resto poco aptos (Aptitud: 1.0). Se ejecuta la selección 100 veces.

Resultado: El individuo más apto (0) fue seleccionado con mucha más frecuencia que los demás. Validado.

Importancia: Confirma el principio de la "supervivencia del más apto" es funcional.

## Autores

- [Peña Lopez Miguel Angel ](https://github.com/KingSplatt)

- [Robles Rios Jacquelin](https://github.com/jacq1813)

