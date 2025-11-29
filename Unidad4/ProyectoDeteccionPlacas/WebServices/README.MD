## Sistema Tercera capa, Web Services (manejo de solicitudes - SERVIDOR)

El archivo requierements.txt contiene las dependencias necesarias para ejecutar el servidor Flask que maneja las solicitudes de la aplicación de detección de placas. Asegúrate de instalar estas dependencias en tu entorno de desarrollo o producción utilizando el siguiente comando:

El archivo Procfile es utilizado por plataformas de despliegue como Heroku para definir cómo se debe ejecutar la aplicación. En este caso le indica a Render como Iniciar el web service usando Gunicorn, un servidor WSGI para aplicaciones Python.

Para que el web services se conecte con la BD (ambos hosteados en render), la cadena de conexión a la base de datos PostgreSQL debe ser configurada en una variable de entorno llamada `DATABASE_URL`. Esta variable debe contener la URL de conexión a la base de datos en el siguiente formato:

```
postgresql://<usuario>:<contraseña>@<host>:<puerto>/<nombre_base_de_datos>
```

Esta cadena se consigue en el panel de Render en el proyecto dedicado para hostear la base de datos PostgreSQL. en el Internal Database URL.

Para el web services debera agregar URL Internal Database URL en la sección de Environment de Render.

## Autores

- [@Peña Lopez Miguel Angel](https://github.com/KingSplatt)

- [@Robles Rios Jacquelin](https://github.com/jacq1813)
