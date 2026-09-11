ALTER TABLE productos
ADD COLUMN ubicacion_id BIGINT;

ALTER TABLE productos
ADD CONSTRAINT fk_producto_ubicacion
FOREIGN KEY (ubicacion_id) REFERENCES ubicaciones(id)
ON DELETE SET NULL;

CREATE INDEX idx_productos_ubicacion_id ON productos(ubicacion_id);