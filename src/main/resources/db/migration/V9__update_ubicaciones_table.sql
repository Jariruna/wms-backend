-- V9__update_ubicaciones_table.sql

-- 1. Renombrar la tabla solo si la tabla antigua existe Y la nueva NO existe aún
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'ubicaciones')
       AND NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'ubicaciones_almacen') THEN
        ALTER TABLE ubicaciones RENAME TO ubicaciones_almacen;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'ubicacion_almacen')
       AND NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'ubicaciones_almacen') THEN
        ALTER TABLE ubicacion_almacen RENAME TO ubicaciones_almacen;
    END IF;
END $$;

-- 2. Crear la tabla únicamente si no existe ninguna
CREATE TABLE IF NOT EXISTS ubicaciones_almacen (
    id BIGSERIAL PRIMARY KEY,
    codigo_ubicacion VARCHAR(50) NOT NULL,
    zona VARCHAR(50) NOT NULL,
    pasillo VARCHAR(20) NOT NULL,
    rack VARCHAR(20) NOT NULL,
    nivel VARCHAR(20) NOT NULL,
    posicion VARCHAR(20),
    capacidad_maxima INTEGER DEFAULT 100 NOT NULL,
    ocupada BOOLEAN NOT NULL DEFAULT FALSE,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT uk_codigo_ubicacion UNIQUE (codigo_ubicacion)
);

-- 3. Renombrar columnas si venían con nombres antiguos de V8
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'ubicaciones_almacen' AND column_name = 'codigo') THEN
        ALTER TABLE ubicaciones_almacen RENAME COLUMN codigo TO codigo_ubicacion;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'ubicaciones_almacen' AND column_name = 'ocupado') THEN
        ALTER TABLE ubicaciones_almacen RENAME COLUMN ocupado TO ocupada;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'ubicaciones_almacen' AND column_name = 'activo') THEN
        ALTER TABLE ubicaciones_almacen RENAME COLUMN activo TO activa;
    END IF;
END $$;

-- 4. Asegurar que existan todas las columnas requeridas
ALTER TABLE ubicaciones_almacen ADD COLUMN IF NOT EXISTS codigo_ubicacion VARCHAR(50);
ALTER TABLE ubicaciones_almacen ADD COLUMN IF NOT EXISTS zona VARCHAR(50);
ALTER TABLE ubicaciones_almacen ADD COLUMN IF NOT EXISTS pasillo VARCHAR(20);
ALTER TABLE ubicaciones_almacen ADD COLUMN IF NOT EXISTS rack VARCHAR(20);
ALTER TABLE ubicaciones_almacen ADD COLUMN IF NOT EXISTS nivel VARCHAR(20);
ALTER TABLE ubicaciones_almacen ADD COLUMN IF NOT EXISTS posicion VARCHAR(20);
ALTER TABLE ubicaciones_almacen ADD COLUMN IF NOT EXISTS capacidad_maxima INTEGER DEFAULT 100;
ALTER TABLE ubicaciones_almacen ADD COLUMN IF NOT EXISTS ocupada BOOLEAN DEFAULT FALSE;
ALTER TABLE ubicaciones_almacen ADD COLUMN IF NOT EXISTS activa BOOLEAN DEFAULT TRUE;

-- 5. Rellenar datos nulos
UPDATE ubicaciones_almacen SET zona = 'GENERAL' WHERE zona IS NULL;
UPDATE ubicaciones_almacen SET pasillo = '01' WHERE pasillo IS NULL;
UPDATE ubicaciones_almacen SET rack = '01' WHERE rack IS NULL;
UPDATE ubicaciones_almacen SET nivel = '01' WHERE nivel IS NULL;
UPDATE ubicaciones_almacen SET capacidad_maxima = 100 WHERE capacidad_maxima IS NULL;

-- 6. Índices
CREATE INDEX IF NOT EXISTS idx_ubicaciones_zona_activa ON ubicaciones_almacen (zona, activa);