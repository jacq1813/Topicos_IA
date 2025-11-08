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

