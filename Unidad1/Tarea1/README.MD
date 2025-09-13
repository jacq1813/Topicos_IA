# Predictive Modeling of Atmospheric Pollution Peaks using Neural Networks

## Resumen del Proyecto

Este repositorio contiene la investigación y el protocolo para el desarrollo de un modelo predictivo de **picos de contaminación atmosférica** en México utilizando **redes neuronales artificiales**. El objetivo principal es integrar variables meteorológicas, urbanas y de tráfico para anticipar episodios de alta contaminación y apoyar la toma de decisiones en la gestión ambiental urbana.


### Objetivo General
Explorar el potencial de diseñar un modelo predictivo de contaminación atmosférica en México utilizando redes neuronales, integrando factores meteorológicos y urbanos para obtener resultados precisos que permitan acciones preventivas y correctivas.

### Objetivos Específicos
- Revisar y sintetizar investigaciones recientes sobre el uso de redes neuronales para la predicción de la calidad del aire
- Identificar las variables más relevantes (meteorológicas, de tráfico, temporales) utilizadas en modelos predictivos
- Explorar el diseño de una arquitectura de RNA que integre algoritmos de aprendizaje supervisado
- Analizar las limitaciones y viabilidad de implementación de estos modelos en contextos reales

## Contexto Nacional

En México, las principales fuentes de contaminantes atmosféricos son:

| Fuente de Emisión | Porcentaje |
|-------------------|------------|
| Fuentes naturales | 35.96% |
| Fuentes móviles carreteras | 28.51% |
| Fuentes de área | 24.37% |
| Fuentes fijas | 10.28% |

Los contaminantes más críticos y sus porcentajes de emisión son:
- **COV (Compuestos Orgánicos Volátiles)**: 42.44%
- **CO (Monóxido de Carbono)**: 30.86%
- **NOx (Óxidos de Nitrógeno)**: 12.41%
- **SO₂ (Dióxido de Azufre)**: 5.27%
- **Material Particulado (PM10 y PM2.5)**: Provenientes principalmente de industrias y agricultura

## Metodología Propuesta

Se propone el uso de arquitecturas de deep learning para modelar la complejidad de los datos atmosféricos:

### Arquitecturas a Evaluar
- **LSTM (Long Short-Term Memory)**: Para capturar dependencias temporales en series de tiempo
- **CNN (Convolutional Neural Networks)**: Para identificar patrones espaciales en datos geoespaciales

### Variables a Integrar
- **Variables Endógenas**: Concentraciones históricas de contaminantes
- **Meteorológicas**: Temperatura, humedad, velocidad del viento, presión
- **Urbanas y de Tráfico**: Volumen vehicular, tipo de vía, datos de calendario
- **Macro-escalares**: Datos de modelos globales de calidad del aire (ej. CAMS)


### Modelo Propuesto
Sistema de predicción basado en redes LSTM, especialmente para el pronostico de concentraciones de contaminantes atmosféricos
 - Para el desarrollo del modelo predictivo, se recopilaran datos provenientes de diversas instituciones mexicanas, obteniendo los datos de contaminantes CO, COV, NH₃, NOx, PM₁₀, PM₂.₅, SO₂

### Fuentes de informacion
- Plataforma del Sistema Nacional de Información de la Calidad del Aire (SINAICA) 
- Sistema de Monitoreo Atmosférico de la Ciudad de México (SIMAT)
- Datos del gobierno del estado

Las variables meteorológicas necesarias se adquirirán del Servicio Meteorológico Nacional (SMN) de la Comisión Nacional del Agua (CONAGUA), incluyendo datos horarios como la temperatura, humedad, velocidad y dirección del viento, precipitación y presión atmosférica. 

##  Autores

- **Miguel Ángel Peña López** 
- **Jacquelin Robles Rios**