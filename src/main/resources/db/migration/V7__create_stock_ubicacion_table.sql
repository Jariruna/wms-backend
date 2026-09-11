CREATE TABLE stock_ubicacion (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    ubicacion_id BIGINT NOT NULL,
    cantidad INT NOT NULL DEFAULT 0,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_ubicacion_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_stock_ubicacion_ubicacion FOREIGN KEY (ubicacion_id) REFERENCES ubicaciones(id),
    CONSTRAINT uk_producto_ubicacion UNIQUE (producto_id, ubicacion_id),
    CONSTRAINT chk_cantidad_no_negativa CHECK (cantidad >= 0)
);

CREATE INDEX idx_stock_ubicacion_producto ON stock_ubicacion(producto_id);
CREATE INDEX idx_stock_ubicacion_ubicacion ON stock_ubicacion(ubicacion_id);