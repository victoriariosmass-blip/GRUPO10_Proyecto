-- Datos de prueba para CU-03 (historial de ventas) y CU-04 (cancelar pedido).
-- Se corre UNA vez en phpMyAdmin (pestaña SQL) sobre la base verdegreen, después de tener las tablas
-- y los datos de prueba de CU-01 y CU-02. Usa ids 101 en adelante para no chocar con los pedidos que
-- se crean desde Swagger. INSERT IGNORE: si se corre dos veces no duplica nada.
USE verdegreen;

-- Pedidos de noviembre 2026
-- 101: ACEPTADO  (Juan, retiro)        -> no es venta; se puede cancelar sin devolución
-- 102: EN_PRODUCCION (Ana, domicilio)  -> es venta; al cancelar avisa a Producción y devuelve enlace (FA-01, FA-02)
-- 103: ENTREGADO (Martin, retiro)      -> es venta; NO se puede cancelar (FE-01)
-- 104: RECHAZADO (Lucia, retiro)       -> no es venta
INSERT IGNORE INTO pedido (id, cliente_id, fecha_creacion, fecha_vencimiento, estado, tipo_entrega, subtotal_productos, costo_envio, costo_personalizacion, total, comprobante, personalizado) VALUES
 (101, 2, '2026-11-10 11:00:00', '2026-11-13 11:00:00', 'ACEPTADO',      'RETIRO',    17000.00,    0.00, 0.00, 17000.00, 'transferencia_juan_101.jpg',  0),
 (102, 1, '2026-11-05 09:15:00', '2026-11-08 09:15:00', 'EN_PRODUCCION', 'DOMICILIO', 36000.00, 3500.00, 0.00, 39500.00, 'transferencia_ana_102.jpg',   0),
 (103, 4, '2026-11-02 16:40:00', '2026-11-05 16:40:00', 'ENTREGADO',     'RETIRO',    12800.00,    0.00, 0.00, 12800.00, 'transferencia_martin_103.jpg',0),
 (104, 3, '2026-11-03 10:00:00', '2026-11-06 10:00:00', 'RECHAZADO',     'RETIRO',     5800.00,    0.00, 0.00,  5800.00, 'transferencia_lucia_104.jpg', 0);

INSERT IGNORE INTO detalle_pedido (id, pedido_id, producto_id, codigo_producto, nombre_producto, cantidad, precio_unitario, subtotal) VALUES
 (101, 101, 3, 'MAC-12',  'Pack macetas N12',         2,  8500.00, 17000.00),
 (102, 102, 1, 'BCL-500', 'Pack bandejas clamshell',  3, 12000.00, 36000.00),
 (103, 103, 4, 'TOR-20',  'Pack torteras',            2,  6400.00, 12800.00),
 (104, 104, 2, 'BRS-60',  'Pack bolsas de residuos',  2,  2900.00,  5800.00);

INSERT IGNORE INTO cambio_estado (id, pedido_id, estado, fecha_hora, observacion) VALUES
 (101, 101, 'ACEPTADO',      '2026-11-10 11:00:00', NULL),
 (102, 102, 'ACEPTADO',      '2026-11-05 09:15:00', NULL),
 (103, 102, 'CONFIRMADO',    '2026-11-05 15:00:00', NULL),
 (104, 102, 'EN_PRODUCCION', '2026-11-06 08:30:00', NULL),
 (105, 103, 'ACEPTADO',      '2026-11-02 16:40:00', NULL),
 (106, 103, 'CONFIRMADO',    '2026-11-03 09:00:00', NULL),
 (107, 103, 'EN_PRODUCCION', '2026-11-03 14:00:00', NULL),
 (108, 103, 'LISTO',         '2026-11-04 12:00:00', NULL),
 (109, 103, 'ENTREGADO',     '2026-11-05 10:30:00', NULL),
 (110, 104, 'ACEPTADO',      '2026-11-03 10:00:00', NULL),
 (111, 104, 'RECHAZADO',     '2026-11-04 09:00:00', NULL);

INSERT IGNORE INTO envio (id, pedido_id, direccion, fecha_programada, observaciones) VALUES
 (101, 102, 'Calle 10 1234, Santa Rosa', NULL, NULL);
