# Sistema de Enrutamiento Óptimo para Distribución (MDVRP) con Recocido Simulado

## Objetivo del Proyecto

Diseñar e implementar una solución computacional basada en la metaheurística de **Recocido Simulado (RS)** para optimizar las rutas de distribución de productos entre múltiples Centros de Distribución (CDs) y una red de 90 sucursales en **Culiacán, Sinaloa**.

El objetivo principal es **minimizar el Costo Total de Distribución** (función objetivo), demostrando la eficiencia de los algoritmos heurísticos frente a un problema de alta complejidad combinatoria (NP-hard).

---

##  Resultados y Logro Principal

La implementación del Recocido Simulado logró una optimización significativa en el problema de Enrutamiento de Vehículos con Múltiples Depósitos (MDVRP):

| Métrica | Solución Inicial (Aleatoria) | Solución Óptima (RS) | Mejora % |
| :--- | :--- | :--- | :--- |
| **Costo Total de Distribución** | ~168.15 | ~34.98 | **~79.20%** |

Los valores exactos varían con cada ejecución debido a la naturaleza probabilística del Recocido Simulado, pero el porcentaje de mejora se mantiene consistentemente alto (79-82%).

---

##  Implementación Técnica

El algoritmo fue implementado en un entorno de desarrollo Python

### 1. Modelo del Problema (MDVRP)

* **Nodos:** 100 Nodos totales (10 Centros de Distribución [CDs] y 90 Tiendas/Clientes).
* **Función Objetivo:** Minimización del **Costo Total de Distribución**, calculado por la suma de los costos de combustible de cada segmento de ruta.
* **Datos:** El algoritmo utiliza tres archivos de entrada esenciales (los que se tomaron en cuenta para la realización del modelo MDVRP): `datos_distribucion_tiendas.xlsx`, `matriz_distancias.xlsx`, y `matriz_costos_combustible.xlsx`.

### 2. Algoritmo Recocido Simulado (RS)

La implementación sigue el **Probabilidad de Boltzzman** con enfriamiento geométrico:

| Elemento | Parámetros y Mecanismo |
| :--- | :--- |
| **Función de Costo** | Calcula el costo iterando sobre cada ruta y sumando los costos de la `MATRIZ_COSTO_TOTAL`.
| **Vecindad Híbrida** | Se seleccionan aleatoriamente dos movimientos de vecindad:1. **Inter-Ruta (Mover):** Mueve una tienda de un CD a otro 2. **Intra-Ruta (Swap):** Intercambia dos tiendas dentro de la misma ruta.
| **Enfriamiento** | **Geométrico:** Tk+1 = Tk * a. El algoritmo inicia con Tinicial=1000.0 y un factor alpha=0.995 para asegurar una exploración lenta y profunda.


### 3. Pseudocodigo Recocido Simulado



```bash
  ALGORITMO RecocidoSimulado(T_inicial, T_final, factor_enfriamiento, iteraciones_por_T):
    // 1. Inicialización
    S_actual = Generar_Solucion_Inicial_Aleatoria()
    E_actual = Calcular_Costo(S_actual)
    S_mejor = S_actual
    E_mejor = E_actual
    T = T_inicial

    // 2. Bucle Principal de Enfriamiento
    MIENTRAS T > T_final:
        // Cadena de Markov (Iteraciones por Temperatura)
        PARA i DE 1 HASTA iteraciones_por_T:
            // 3. Generación de Vecindad
            S_vecina = Generar_Solucion_Vecina(S_actual) // Usando Inter-Ruta o Intra-Ruta
            E_vecina = Calcular_Costo(S_vecina)
            
            Delta_E = E_vecina - E_actual

            // 4. Criterio de Metrópolis (Decisión de Movimiento)
            SI Delta_E < 0:
                // Aceptar la solución vecina (es mejor)
                S_actual = S_vecina
                E_actual = E_vecina
            SINO:
                // Aceptar con Probabilidad (Permitir escapar de óptimos locales)
                P = e^(-Delta_E / T)
                SI Random(0, 1) < P:
                    S_actual = S_vecina
                    E_actual = E_vecina

            // 5. Actualizar Mejor Solución Global
            SI E_actual < E_mejor:
                S_mejor = S_actual
                E_mejor = E_actual

        // 6. Enfriamiento Geométrico
        T = T * factor_enfriamiento

    RETORNAR S_mejor, E_mejor
```


## Autores

- [Peña Lopez Miguel Angel ](https://github.com/KingSplatt)

- [Robles Rios Jacquelin](https://github.com/jacq1813)



## Referencias

[Peralta Abarca , J. del C. ., Juárez Chávez , J. Y. ., & Martínez Bahena, B. . (2021). Aplicaciones de recocido simulado en problemas de optimización combinatoria. Inventio, 11(23), 23–28. Recuperado a partir de ](https://inventio.uaem.mx/index.php/inventio/article/view/281)

[Álvaro, L. B., & Felipe, R. T. A. (2015). Solución del problema de restauración de sistemas de distribución usando los métodos de optimización metaheurísticos recocido simulado y búsqueda tabú. Repositorio De La Universidad Tecnológica De Pereira. ](https://repositorio.utp.edu.co/entities/publication/1624db50-639e-4d48-83da-60cb8999ddc6)


[Osman, I. H. (1993). Metastrategy simulated annealing and tabu search algorithms for the vehicle routing problem. Annals of Operations Research, 41(4), 421–451. ](https://doi.org/10.1007/bf02023004)
