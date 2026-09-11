-- Creación del tipo ENUM para clasificar el tipo de movimiento
CREATE TYPE tipo_movimiento_enum AS ENUM ('ENTRADA', 'SALIDA', 'AJUSTE');

-- Creación de la tabla de Kardex / Movimientos de Almacén
CREATE TABLE movimientos_almacen (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    stock_anterior INT NOT NULL,
    stock_resultante INT NOT NULL,
    motivo VARCHAR(255),
    usuario VARCHAR(100) NOT NULL,
    fecha_movimiento TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (producto_id) REFERENCES productos (id),
    CONSTRAINT chk_cantidad_positiva CHECK (cantidad > 0),
    CONSTRAINT chk_tipo_movimiento CHECK (tipo_movimiento IN ('ENTRADA', 'SALIDA', 'AJUSTE'))
);

-- Índices para optimización de consultas de auditoría y Kardex por producto
CREATE INDEX idx_movimiento_producto_id ON movimientos_almacen (producto_id);
CREATE INDEX idx_movimiento_fecha ON movimientos_almacen (fecha_movimiento DESC);