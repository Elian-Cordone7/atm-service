INSERT INTO tarjeta (numero_tarjeta, activa) VALUES
('1234567890123456', true),
('9876543210987654', true);


INSERT INTO cuenta (numero_cuenta, saldo, activa) VALUES
('0001', 5000.00, true),
('0002', 3000.00, true);


INSERT INTO tarjeta_cuenta (tarjeta_id, cuenta_id)
SELECT t.id, c.id
FROM tarjeta t
JOIN cuenta c ON c.numero_cuenta = '0001'
WHERE t.numero_tarjeta = '1234567890123456';

INSERT INTO tarjeta_cuenta (tarjeta_id, cuenta_id)
SELECT t.id, c.id
FROM tarjeta t
JOIN cuenta c ON c.numero_cuenta = '0002'
WHERE t.numero_tarjeta = '9876543210987654';
