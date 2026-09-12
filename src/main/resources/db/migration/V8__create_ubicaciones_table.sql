CREATE TABLE IF NOT EXISTS ubicaciones (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    zona VARCHAR(50) NOT NULL,
    pasillo VARCHAR(20),
    rack VARCHAR(20),
    nivel VARCHAR(20),
    capacidad_maxima INTEGER NOT NULL DEFAULT 100,
    ocupado BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ubicaciones_codigo ON ubicaciones(codigo);
CREATE INDEX IF NOT EXISTS idx_ubicaciones_zona ON ubicaciones(zona);