CREATE TABLE tarjeta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_tarjeta VARCHAR(20) NOT NULL UNIQUE,
    activa BOOLEAN NOT NULL
);

CREATE TABLE cuenta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    saldo DECIMAL(15,2) NOT NULL,
    activa BOOLEAN NOT NULL
);

CREATE TABLE tarjeta_cuenta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tarjeta_id BIGINT NOT NULL,
    cuenta_id BIGINT NOT NULL,
    FOREIGN KEY (tarjeta_id) REFERENCES tarjeta(id),
    FOREIGN KEY (cuenta_id) REFERENCES cuenta(id),
    UNIQUE (tarjeta_id, cuenta_id)
);

CREATE TABLE transaccion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_transaccion VARCHAR(30) NOT NULL,
    numero_tarjeta VARCHAR(30) NOT NULL,
    numero_cuenta VARCHAR(30),
    monto DECIMAL(15,2),
    saldo_anterior DECIMAL(15,2),
    saldo_posterior DECIMAL(15,2),
    fecha_transaccion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL,
    motivo_error VARCHAR(100),
    codigo_error VARCHAR(50),
    detalles_adicionales VARCHAR(255)
);
