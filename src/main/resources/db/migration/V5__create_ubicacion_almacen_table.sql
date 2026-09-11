CREATE TABLE IF NOT EXISTS ubicacion_almacen (
    id BIGSERIAL PRIMARY KEY,
    codigo_ubicacion VARCHAR(30) NOT NULL UNIQUE,
    pasillo VARCHAR(10) NOT NULL,
    rack VARCHAR(10) NOT NULL,
    nivel VARCHAR(10) NOT NULL,
    posicion VARCHAR(10),
    capacidad_maxima INT NOT NULL DEFAULT 100,
    ocupada BOOLEAN NOT NULL DEFAULT FALSE,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ubicacion_coordenadas ON ubicacion_almacen (pasillo, rack, nivel);