ALTER TABLE movimientos_almacen
ADD COLUMN ubicacion_id BIGINT;

ALTER TABLE movimientos_almacen
ADD CONSTRAINT fk_movimiento_ubicacion
FOREIGN KEY (ubicacion_id) REFERENCES ubicaciones(id);

CREATE INDEX idx_movimiento_ubicacion ON movimientos_almacen(ubicacion_id);