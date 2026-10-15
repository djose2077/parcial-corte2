-- ================================================================
--  Script MySQL — parcial_tarjetas_jose
--  HU-01: Estado de la tarjeta (ACTIVA | BLOQUEADA | CANCELADA)
--
--  Estrategia: JOINED TABLE (una tabla por clase)
--    titular           → Titular
--    tarjeta           → Tarjeta (abstracta, campos comunes)
--    tarjeta_debito    → Debito  extends Tarjeta (1:1)
--    tarjeta_credito   → Credito extends Tarjeta (1:1)
--
--  Cambio HU-01: tarjeta.estado pasa de TINYINT(1) a VARCHAR(10)
--  con CHECK, obligatorio y sin valor por defecto.
-- ================================================================

CREATE DATABASE IF NOT EXISTS parcial_tarjetas_jose
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE parcial_tarjetas_jose;

-- ---------------------------------------------------------------
-- TABLA 1 — titular
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS titular (
    id      VARCHAR(50)  NOT NULL,
    nombre  VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------
-- TABLA 2 — tarjeta
-- estado: obligatorio, sin DEFAULT, solo 3 valores válidos (HU-01)
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tarjeta (
    numero      VARCHAR(20)  NOT NULL,
    fecha_exp   VARCHAR(7)   NOT NULL COMMENT 'Formato MM/AAAA',
    estado      VARCHAR(10)  NOT NULL COMMENT 'ACTIVA | BLOQUEADA | CANCELADA',
    titular_id  VARCHAR(50)  NOT NULL,

    PRIMARY KEY (numero),

    CONSTRAINT chk_tarjeta_estado
        CHECK (estado IN ('ACTIVA', 'BLOQUEADA', 'CANCELADA')),

    CONSTRAINT fk_tarjeta_titular
        FOREIGN KEY (titular_id)
        REFERENCES  titular (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB;

-- ---------------------------------------------------------------
-- TABLA 3 — tarjeta_debito
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tarjeta_debito (
    numero  VARCHAR(20) NOT NULL,
    saldo   DOUBLE      NOT NULL DEFAULT 0.0,

    PRIMARY KEY (numero),

    CONSTRAINT fk_debito_tarjeta
        FOREIGN KEY (numero)
        REFERENCES  tarjeta (numero)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------
-- TABLA 4 — tarjeta_credito
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tarjeta_credito (
    numero  VARCHAR(20) NOT NULL,
    limite  DOUBLE      NOT NULL DEFAULT 0.0,

    PRIMARY KEY (numero),

    CONSTRAINT fk_credito_tarjeta
        FOREIGN KEY (numero)
        REFERENCES  tarjeta (numero)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------
-- Datos de prueba
-- ---------------------------------------------------------------
INSERT INTO titular (id, nombre) VALUES
    ('T001', 'John Marston'),
    ('T002', 'Arthur Morgan');

INSERT INTO tarjeta (numero, fecha_exp, estado, titular_id) VALUES
    ('4111111111111111', '12/2027', 'ACTIVA',    'T001'),
    ('5500005555555559', '06/2026', 'ACTIVA',    'T001'),
    ('4000056655665556', '09/2025', 'BLOQUEADA', 'T002'),
    ('5105105105105100', '03/2027', 'CANCELADA', 'T002');

INSERT INTO tarjeta_debito (numero, saldo) VALUES
    ('4111111111111111', 1500000.00),
    ('4000056655665556',  800000.00);

INSERT INTO tarjeta_credito (numero, limite) VALUES
    ('5500005555555559', 5000000.00),
    ('5105105105105100', 2000000.00);

-- ---------------------------------------------------------------
-- Pruebas rápidas de la restricción (deben FALLAR):
-- INSERT INTO tarjeta VALUES ('999','01/2030','XYZ','T001');   -- estado inválido
-- INSERT INTO tarjeta (numero, fecha_exp, titular_id)
--        VALUES ('998','01/2030','T001');                      -- sin estado
-- ---------------------------------------------------------------
