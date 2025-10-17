-- ============================================
-- CrudPark - Base de Datos PostgreSQL
-- ============================================

-- CREATE DATABASE crudpark;
-- \c crudpark;

-- ============================================
-- TABLAS
-- ============================================

-- 1. Operadores
CREATE TABLE operadores (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Mensualidades
CREATE TABLE mensualidades (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    placa VARCHAR(20) UNIQUE NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activo BOOLEAN DEFAULT true,
    CONSTRAINT chk_fechas CHECK (fecha_fin >= fecha_inicio)
);

-- 3. Tarifas
CREATE TABLE tarifas (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    tipo_vehiculo VARCHAR(20) NOT NULL CHECK (tipo_vehiculo IN ('CARRO', 'MOTO')),
    valor_hora DECIMAL(10,2) NOT NULL CHECK (valor_hora >= 0),
    tiempo_gracia INT DEFAULT 30 CHECK (tiempo_gracia >= 0),
    activo BOOLEAN DEFAULT true
);

-- 4. Tickets
CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    placa VARCHAR(20) NOT NULL,
    tipo_cliente VARCHAR(20) NOT NULL CHECK (tipo_cliente IN ('MENSUALIDAD', 'INVITADO')),
    tipo_vehiculo VARCHAR(20) NOT NULL CHECK (tipo_vehiculo IN ('CARRO', 'MOTO')),
    fecha_ingreso TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_salida TIMESTAMP,
    folio VARCHAR(20) UNIQUE,
    operador_ingreso_id INT NOT NULL REFERENCES operadores(id),
    operador_salida_id INT REFERENCES operadores(id),
    estado VARCHAR(20) DEFAULT 'ABIERTO' CHECK (estado IN ('ABIERTO', 'CERRADO', 'GRACIA')),
    qr_code TEXT,
    monto_cobrado DECIMAL(10,2) DEFAULT 0 CHECK (monto_cobrado >= 0),
    CONSTRAINT chk_salida CHECK (fecha_salida IS NULL OR fecha_salida >= fecha_ingreso)
);

-- 5. Pagos
CREATE TABLE pagos (
    id SERIAL PRIMARY KEY,
    ticket_id INT NOT NULL REFERENCES tickets(id),
    monto DECIMAL(10,2) NOT NULL CHECK (monto > 0),
    metodo_pago VARCHAR(50) NOT NULL,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operador_id INT NOT NULL REFERENCES operadores(id)
);

-- ============================================
-- ÍNDICES IMPORTANTES
-- ============================================

-- Solo un ticket abierto por placa
CREATE UNIQUE INDEX idx_ticket_abierto_unico ON tickets(placa) WHERE estado = 'ABIERTO';

-- Performance
CREATE INDEX idx_tickets_placa ON tickets(placa);
CREATE INDEX idx_tickets_estado ON tickets(estado);
CREATE INDEX idx_mensualidades_placa ON mensualidades(placa);

-- ============================================
-- VISTAS ÚTILES
-- ============================================

-- Vehículos actualmente dentro
CREATE VIEW vehiculos_dentro AS
SELECT
    t.id,
    t.placa,
    t.tipo_vehiculo,
    t.tipo_cliente,
    t.fecha_ingreso,
    o.nombre AS operador,
    EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - t.fecha_ingreso))/60 AS minutos_dentro
FROM tickets t
JOIN operadores o ON t.operador_ingreso_id = o.id
WHERE t.estado = 'ABIERTO';

-- Mensualidades vigentes
CREATE VIEW mensualidades_vigentes AS
SELECT * FROM mensualidades
WHERE activo = true AND CURRENT_DATE BETWEEN fecha_inicio AND fecha_fin;

-- ============================================
-- DATOS DE PRUEBA
-- ============================================

INSERT INTO operadores (nombre, usuario, password, email, activo) VALUES
('Admin', 'admin', '123456', 'admin@crudpark.com', true),
('Juan Pérez', 'jperez', '123456', 'jperez@crudpark.com', true);

INSERT INTO tarifas (nombre, tipo_vehiculo, valor_hora, tiempo_gracia, activo) VALUES
('Tarifa Carro', 'CARRO', 5000.00, 30, true),
('Tarifa Moto', 'MOTO', 3000.00, 30, true);

INSERT INTO mensualidades (nombre, email, placa, fecha_inicio, fecha_fin) VALUES
('Carlos Mendez', 'cmendez@example.com', 'ABC123', CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days');
