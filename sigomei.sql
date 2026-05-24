-- =============================================================================
-- SIGOMEI — Script SQL Final
-- Desarrollo de Sistemas en Red — E4
-- Rogelio Rojas Santos
-- =============================================================================

CREATE DATABASE IF NOT EXISTS sigomei
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;

USE sigomei;

-- =============================================================================
-- TABLAS
-- =============================================================================

DROP TABLE IF EXISTS orden_mantenimiento;
DROP TABLE IF EXISTS tecnico;
DROP TABLE IF EXISTS equipo_industrial;

CREATE TABLE equipo_industrial (
    id_equipo          INT          NOT NULL AUTO_INCREMENT,
    nombre             VARCHAR(100) NOT NULL,
    tipo               ENUM('Electrico','Mecanico','Instrumentacion','Hidraulico') NOT NULL,
    marca              VARCHAR(80)  NOT NULL,
    modelo             VARCHAR(80)  NULL,
    numero_serie       VARCHAR(80)  NULL,
    ubicacion_planta   VARCHAR(120) NULL,
    fecha_instalacion  DATE         NULL,
    estado_operativo   ENUM('Activo','Mantenimiento','Fuera de operacion') NOT NULL DEFAULT 'Activo',
    criticidad         ENUM('Baja','Media','Alta') NOT NULL,
    CONSTRAINT pk_equipo_industrial PRIMARY KEY (id_equipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------

CREATE TABLE tecnico (
    id_tecnico           INT          NOT NULL AUTO_INCREMENT,
    nombre_completo      VARCHAR(120) NOT NULL,
    rfc                  VARCHAR(20)  NOT NULL,
    telefono             VARCHAR(30)  NULL,
    correo               VARCHAR(120) NULL,
    especialidad         ENUM('Electrico','Mecanico','Instrumentacion','Hidraulico') NOT NULL,
    nivel_certificacion  ENUM('I','II','III') NOT NULL,
    fecha_ingreso        DATE         NULL,
    estatus              ENUM('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
    CONSTRAINT pk_tecnico PRIMARY KEY (id_tecnico)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------

CREATE TABLE orden_mantenimiento (
    id_orden         INT          NOT NULL AUTO_INCREMENT,
    descripcion      TEXT         NOT NULL,
    estado           ENUM('Programada','En ejecucion','Finalizada','Cancelada')
                                  NOT NULL DEFAULT 'Programada',
    id_equipo        INT          NOT NULL,
    id_tecnico       INT          NOT NULL,
    fecha_programada DATE         NOT NULL,
    fecha_inicio     DATE             NULL,
    fecha_cierre     DATE             NULL,
    CONSTRAINT pk_orden_mantenimiento PRIMARY KEY (id_orden),
    CONSTRAINT fk_ord_equipo FOREIGN KEY (id_equipo)  REFERENCES equipo_industrial(id_equipo),
    CONSTRAINT fk_ord_tecnico FOREIGN KEY (id_tecnico) REFERENCES tecnico(id_tecnico)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- ÍNDICES
-- =============================================================================

CREATE INDEX idx_equipo_tipo        ON equipo_industrial(tipo);
CREATE INDEX idx_equipo_criticidad  ON equipo_industrial(criticidad);

CREATE INDEX idx_tecnico_especialidad ON tecnico(especialidad);
CREATE INDEX idx_tecnico_estatus      ON tecnico(estatus);

CREATE INDEX idx_orden_equipo   ON orden_mantenimiento(id_equipo);
CREATE INDEX idx_orden_tecnico  ON orden_mantenimiento(id_tecnico);
CREATE INDEX idx_orden_estado   ON orden_mantenimiento(estado);
CREATE INDEX idx_orden_fecha    ON orden_mantenimiento(fecha_programada);

-- =============================================================================
-- DATOS DE PRUEBA
-- =============================================================================

INSERT INTO equipo_industrial
    (nombre, tipo, marca, modelo, numero_serie, ubicacion_planta, fecha_instalacion, estado_operativo, criticidad)
VALUES
  ('Motor Electrico A',    'Electrico',      'Siemens',        'M1',   'SN-1001', 'Planta 1', '2024-01-10', 'Activo',        'Baja'),
  ('Bomba Hidraulica B',   'Hidraulico',     'Bosch',          'H2',   'SN-2002', 'Planta 2', '2023-11-05', 'Activo',        'Media'),
  ('Compresor C1',         'Mecanico',       'Atlas Copco',    'C1',   'SN-3003', 'Planta 1', '2022-09-18', 'Mantenimiento', 'Media'),
  ('Sensor Presion S1',    'Instrumentacion','Endress+Hauser', 'S1',   'SN-4004', 'Planta 3', '2021-06-22', 'Activo',        'Alta');

INSERT INTO tecnico
    (nombre_completo, rfc, telefono, correo, especialidad, nivel_certificacion, fecha_ingreso, estatus)
VALUES
  ('Ana Torres',   'TORA900101AAA', '555-1001', 'ana@sigomei.com',   'Electrico',       'I',  '2022-01-15', 'Activo'),
  ('Luis Mendoza', 'MELU910202BBB', '555-1002', 'luis@sigomei.com',  'Mecanico',        'I',  '2021-03-20', 'Activo'),
  ('Rosa Diaz',    'DARO920303CCC', '555-1003', 'rosa@sigomei.com',  'Instrumentacion','II', '2020-08-10', 'Activo'),
  ('Ivan Mora',    'MOIV930404DDD', '555-1004', 'ivan@sigomei.com',  'Hidraulico',      'I',  '2019-11-05', 'Activo');

INSERT INTO orden_mantenimiento
    (descripcion, estado, id_equipo, id_tecnico, fecha_programada)
VALUES
  ('Mantenimiento preventivo Motor A',   'Programada',   1, 1, '2026-06-01'),
  ('Revision Bomba Hidraulica B',        'En ejecucion', 2, 4, '2026-05-15'),
  ('Calibracion Sensor Presion S1',      'Finalizada',   4, 3, '2026-05-01');

-- Establecer fechas de inicio y cierre para orden finalizada
UPDATE orden_mantenimiento
   SET fecha_inicio = '2026-05-05', fecha_cierre = '2026-05-10'
 WHERE descripcion = 'Calibracion Sensor Presion S1';

-- Establecer fecha de inicio para orden en ejecucion
UPDATE orden_mantenimiento
   SET fecha_inicio = '2026-05-15'
  WHERE descripcion = 'Revision Bomba Hidraulica B';
