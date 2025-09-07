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
