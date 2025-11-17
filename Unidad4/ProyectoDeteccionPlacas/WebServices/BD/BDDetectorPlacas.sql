CREATE TABLE Propietario (
    PropietarioID SERIAL PRIMARY KEY, --auto increment
    Nombre VARCHAR(100) NOT NULL,
    Correo VARCHAR(100) UNIQUE NOT NULL,
    Telefono VARCHAR(15),
    CURP VARCHAR(18) UNIQUE, 
    Direccion TEXT
);

CREATE TABLE Usuario (
    UsuarioID SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Contrasena VARCHAR(255) NOT NULL,
    Correo VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE Vehiculos (
    NumPlaca VARCHAR(20) PRIMARY KEY, 
    Modelo VARCHAR(50),
    Marca VARCHAR(50),
    Anio INTEGER,
    PropietarioID INTEGER NOT NULL,
    CONSTRAINT fk_propietario
        FOREIGN KEY (PropietarioID)
        REFERENCES Propietario (PropietarioID)
        ON DELETE RESTRICT -- no borra propietarios si tiene vehiculos
);

CREATE TABLE Reporte (
    ReporteID SERIAL PRIMARY KEY,
    FechaEmision TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Coordenadas VARCHAR(100),
    Descripcion TEXT,
    ImgEvidencia VARCHAR(255), -- url de cloudinary
    NumPlaca VARCHAR(20) NOT NULL,
    UsuarioID INTEGER NOT NULL,
    
    CONSTRAINT fk_vehiculo
        FOREIGN KEY (NumPlaca)
        REFERENCES Vehiculos (NumPlaca)
        ON DELETE RESTRICT,
        
    CONSTRAINT fk_usuario
        FOREIGN KEY (UsuarioID)
        REFERENCES Usuario (UsuarioID)
        ON DELETE RESTRICT
);

-- Indices para optimizar consultas frecuentes
CREATE INDEX idx_reporte_numplaca ON Reporte (NumPlaca);
CREATE INDEX idx_reporte_usuarioid ON Reporte (UsuarioID);
CREATE INDEX idx_vehiculos_propietarioid ON Vehiculos (PropietarioID);

-- 1. INSERCIÓN DE DATOS DE PROPIETARIO (100 REGISTROS) 
-- Colonias de Culiacán, Sinaloa para direcciones
-- Fuente: Ficticia basada en nombres comunes de Culiacán
DO $$
DECLARE
    i INT;
    nombre_propietario VARCHAR[] := ARRAY['Jesús', 'María', 'José', 'Ana', 'Luis', 'Sofía', 'Carlos', 'Elena', 'Ricardo', 'Valeria', 'Javier', 'Adriana', 'Miguel', 'Fernanda', 'Héctor', 'Paola', 'Gustavo', 'Diana', 'Roberto', 'Camila'];
    apellido_propietario VARCHAR[] := ARRAY['López', 'Pérez', 'Gómez', 'Rodríguez', 'Sánchez', 'Martínez', 'García', 'Flores', 'Ramírez', 'Díaz', 'Torres', 'Vargas', 'Reyes', 'Mendoza', 'Hernández', 'Castro', 'Ruiz', 'Silva', 'Ceniceros', 'Zazueta'];
    colonias VARCHAR[] := ARRAY['Tierra Blanca', 'Tres Ríos', 'Infonavit Humaya', 'Centro', 'Las Quintas', 'Guadalupe', 'Jorge Almada', 'Industrial El Palmito', 'Chapultepec', 'Villa Satélite', 'Cumbres', 'Universitaria', 'Emiliano Zapata', 'Revolución', 'Campiña'];
    nombre_generado VARCHAR;
    apellido_paterno VARCHAR;
    apellido_materno VARCHAR;
    curp_ficticio CHAR(18);
    correo_generado VARCHAR;
    telefono_generado VARCHAR;
    direccion_generada TEXT;
BEGIN
    FOR i IN 1..100 LOOP
        -- Generación de datos
        apellido_paterno := apellido_propietario[1 + (i % array_length(apellido_propietario, 1))];
        apellido_materno := apellido_propietario[1 + ((i + 5) % array_length(apellido_propietario, 1))];
        nombre_generado := nombre_propietario[1 + (i % array_length(nombre_propietario, 1))] || ' ' || apellido_paterno || ' ' || apellido_materno;
        
        -- Curp muy básico para simulación, asegurando 18 caracteres
        curp_ficticio := 'XXXX' || LPAD(i::text, 6, '0') || 'SNAH' || LPAD((i + 1)::text, 2, '0') || LPAD((i % 100)::text, 2, '0');
        -- Correo único
        correo_generado := LOWER(REPLACE(nombre_propietario[1 + (i % array_length(nombre_propietario, 1))], ' ', '')) || i::text || '@culiacanmail.com';
        
        -- Teléfono (Formato de Sinaloa 667-XXX-XXXX)
        telefono_generado := '667-' || LPAD((5000000 + i)::text, 7, '0');
        
        -- Dirección con colonia de Culiacán
        direccion_generada := 'Calle # ' || (i * 10) || ', Col. ' || colonias[1 + (i % array_length(colonias, 1))] || ', Culiacán, Sinaloa.';

        -- Inserción
        INSERT INTO Propietario (Nombre, Correo, Telefono, CURP, Direccion)
        VALUES (nombre_generado, correo_generado, telefono_generado, curp_ficticio, direccion_generada);
    END LOOP;
END $$;

--2. INSERCIÓN DE DATOS DE VEHICULOS (100 REGISTROS)
-- Marcas, modelos y años ficticios pero comunes en Culiacán
DO $$
DECLARE
    i INT;
    marcas VARCHAR[] := ARRAY['Nissan', 'Chevrolet', 'Ford', 'Honda', 'Toyota', 'Audi', 'Volkswagen', 'Mazda', 'BMW', 'Kia'];
    modelos VARCHAR[] := ARRAY['Versa', 'Cheyenne', 'Explorer', 'Civic', 'Corolla', 'A4', 'Jetta', '3', 'X3', 'Sportage', 'Sentra', 'Silverado', 'F-150', 'CR-V', 'Tacoma', 'Q5', 'Tiguan', 'CX-5', 'Serie 3', 'Sorento'];
    letras_placa CHAR[] := ARRAY['V', 'R', 'M', 'P', 'G', 'K', 'L', 'S', 'H', 'B'];
    marca_vehiculo VARCHAR;
    modelo_vehiculo VARCHAR;
    anio_vehiculo INT;
    num_placa_generada VARCHAR;
BEGIN
    FOR i IN 1..100 LOOP
        -- Generación de datos
        marca_vehiculo := marcas[1 + (i % array_length(marcas, 1))];
        modelo_vehiculo := modelos[1 + ((i + 3) % array_length(modelos, 1))];
        anio_vehiculo := 2005 + (i % 20); -- Años entre 2005 y 2024
        
        -- Placa de Sinaloa (simulada: 3 letras + 4 números, ej: VRM-9901)
        num_placa_generada := letras_placa[1 + (i % 10)] || letras_placa[1 + ((i+1) % 10)] || letras_placa[1 + ((i+2) % 10)] || '-' || LPAD(i::text, 4, '0');
        
        -- Inserción. Asignamos al PropietarioID 'i' (1 al 100)
        INSERT INTO Vehiculos (NumPlaca, Modelo, Marca, Anio, PropietarioID)
        VALUES (num_placa_generada, modelo_vehiculo, marca_vehiculo, anio_vehiculo, i);
    END LOOP;
END $$;