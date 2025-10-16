-- Eliminar tablas si existen (en orden inverso de creación para manejar dependencias)
DROP TABLE IF EXISTS metricas CASCADE;
DROP TABLE IF EXISTS ranking CASCADE;
DROP TABLE IF EXISTS puntajes CASCADE;
DROP TABLE IF EXISTS juegos CASCADE;
DROP TABLE IF EXISTS preguntas CASCADE;
DROP TABLE IF EXISTS cargas CASCADE;
DROP TABLE IF EXISTS tipos_carga CASCADE;
DROP TABLE IF EXISTS estados_carga CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;
DROP TABLE IF EXISTS asignaturas_semestre CASCADE;
DROP TABLE IF EXISTS asignaturas CASCADE;
DROP TABLE IF EXISTS semestres CASCADE;
DROP TABLE IF EXISTS carreras CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS instituciones CASCADE;
DROP TABLE IF EXISTS comunas CASCADE;
DROP TABLE IF EXISTS regiones CASCADE;
DROP TABLE IF EXISTS paises CASCADE;

-- Extensión para generar UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--- Tablas de Ubicación e Institución
CREATE TABLE paises (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        nombre VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE regiones (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          nombre VARCHAR(100) NOT NULL UNIQUE,
                          id_pais UUID NOT NULL,
                          FOREIGN KEY (id_pais) REFERENCES paises(id)
);
CREATE TABLE comunas (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         nombre VARCHAR(100) NOT NULL UNIQUE,
                         id_region UUID NOT NULL,
                         FOREIGN KEY (id_region) REFERENCES regiones(id)
);
CREATE TABLE instituciones (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               nombre VARCHAR(255) NOT NULL UNIQUE,
                               id_comuna UUID,
                               FOREIGN KEY (id_comuna) REFERENCES comunas(id)
);
--
-- Tablas de Roles y Contenido
--
CREATE TABLE roles (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       nombre VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE carreras (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          nombre VARCHAR(100) NOT NULL,
                          id_institucion UUID NOT NULL,
                          FOREIGN KEY (id_institucion) REFERENCES instituciones(id)
);
CREATE TABLE semestres (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           nombre VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE asignaturas (
                             id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             nombre VARCHAR(100) NOT NULL,
                             id_carrera UUID NOT NULL,
                             FOREIGN KEY (id_carrera) REFERENCES carreras(id)
);
-- Tabla intermedia para la relación N:M entre Asignatura y Semestre
CREATE TABLE asignaturas_semestre (
                                      id_asignatura UUID NOT NULL,
                                      id_semestre UUID NOT NULL,
                                      PRIMARY KEY (id_asignatura, id_semestre),
                                      FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
                                      FOREIGN KEY (id_semestre) REFERENCES semestres(id)
);
--
-- Tablas de Usuarios
--
CREATE TABLE usuarios (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          nombre VARCHAR(100) NOT NULL,
                          apellido VARCHAR(100) NOT NULL,
                          id_rol UUID NOT NULL,
                          id_carrera UUID NOT NULL,
                          password_hash VARCHAR(255) NOT NULL,
                          correo VARCHAR(100) NOT NULL UNIQUE,
                          estado BOOLEAN NOT NULL DEFAULT TRUE,
                          celular VARCHAR(20),
                          fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          fecha_ultimo_login TIMESTAMP,
                          FOREIGN KEY (id_rol) REFERENCES roles(id),
                          FOREIGN KEY (id_carrera) REFERENCES carreras(id)
);
--
-- Tablas de Carga y Auditoría
--
CREATE TABLE estados_carga (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               nombre_estado VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE tipos_carga (
                             id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             nombre_tipo VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE cargas (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        id_usuario_carga UUID NOT NULL,
                        fecha_hora_carga TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        nombre_archivo VARCHAR(255) NOT NULL,
                        id_estado UUID NOT NULL,
                        detalle_error TEXT,
                        id_tipo_carga UUID NOT NULL,
                        FOREIGN KEY (id_usuario_carga) REFERENCES usuarios(id),
                        FOREIGN KEY (id_estado) REFERENCES estados_carga(id),
                        FOREIGN KEY (id_tipo_carga) REFERENCES tipos_carga(id)
);
--
-- Tablas de Gamificación
--
CREATE TABLE preguntas (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           texto TEXT NOT NULL,
                           respuesta_correcta TEXT NOT NULL,
                           tema VARCHAR(100),
                           id_asignatura UUID NOT NULL,
                           FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);
CREATE TABLE juegos (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        id_usuario UUID NOT NULL,
                        id_asignatura UUID NOT NULL,
                        fecha_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        fecha_fin TIMESTAMP,
                        puntaje NUMERIC(10, 2),
                        FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
                        FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);
CREATE TABLE puntajes (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          id_usuario UUID NOT NULL,
                          id_asignatura UUID NOT NULL,
                          puntaje NUMERIC(10, 2) NOT NULL,
                          fecha_asignacion DATE NOT NULL DEFAULT CURRENT_DATE,
                          FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
                          FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);
CREATE TABLE ranking (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         id_usuario UUID NOT NULL,
                         id_asignatura UUID NOT NULL,
                         puntaje_total NUMERIC(10, 2) NOT NULL,
                         posicion INT NOT NULL,
                         FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
                         FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);
CREATE TABLE metricas (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          id_usuario UUID NOT NULL,
                          id_pregunta UUID NOT NULL,
                          respuesta_correcta BOOLEAN NOT NULL,
                          tiempo_respuesta_ms INT,
                          fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
                          FOREIGN KEY (id_pregunta) REFERENCES preguntas(id)
);

/******************************************************************/
/*********************** INSERCIÓN DE DATOS ***********************/
/******************************************************************/

WITH ins_pais AS (
    INSERT INTO paises (nombre) VALUES ('Chile') RETURNING id
),
     ins_region AS (
         INSERT INTO regiones (nombre, id_pais)
             SELECT 'Metropolitana', id FROM ins_pais RETURNING id
     ),
     ins_comuna AS (
         INSERT INTO comunas (nombre, id_region)
             SELECT 'Cerrillos', id FROM ins_region RETURNING id
     ),
     ins_institucion AS (
         INSERT INTO instituciones (nombre, id_comuna)
             SELECT 'DUOC UC Sede Plaza Oeste', id FROM ins_comuna RETURNING id
     ),
     ins_rol_1 AS (
         INSERT INTO roles (nombre) VALUES ('admin') RETURNING id

     ),
     ins_rol_2 AS (
         INSERT INTO roles (nombre) VALUES ('profesor') RETURNING id

     ),
     ins_rol_3 AS (
         INSERT INTO roles (nombre) VALUES ('alumno') RETURNING id

     ),
     ins_semestre AS (
         INSERT INTO semestres (nombre) VALUES ('Primer Semestre 2024') RETURNING id
     ),
     ins_carrera AS (
         INSERT INTO carreras (nombre, id_institucion)
             SELECT 'Ingeniería en Informática', id FROM ins_institucion RETURNING id
     ),
     ins_asignatura AS (
         INSERT INTO asignaturas (nombre, id_carrera)
             SELECT 'Algoritmos y Programación', id FROM ins_carrera RETURNING id
     ),
     ins_asignatura_semestre AS (
         INSERT INTO asignaturas_semestre (id_asignatura, id_semestre)
             SELECT ins_asignatura.id, ins_semestre.id FROM ins_asignatura, ins_semestre
     ),
     ins_usuario AS (
         INSERT INTO usuarios (nombre, apellido, id_rol, id_carrera, password_hash, correo)
             SELECT 'Cecilia', 'Arroyo', ins_rol_1.id, ins_carrera.id, '$2a$10$NEiiAWhPBEU5Vjf6rf/jmONRoPMq5hTiDEEubaq1NMkfrW3lvJh.m', 'cecilia.arroyo@duoc.cl'
             FROM ins_rol_1, ins_carrera RETURNING id
     ),
     ins_estado_carga AS (
         INSERT INTO estados_carga (nombre_estado) VALUES ('Completado') RETURNING id
     ),
     ins_tipo_carga AS (
         INSERT INTO tipos_carga (nombre_tipo) VALUES ('Carga Masiva Apuntes') RETURNING id
     ),
     ins_carga AS (
         INSERT INTO cargas (id_usuario_carga, nombre_archivo, id_estado, id_tipo_carga)
             SELECT ins_usuario.id, 'apuntes_semana_1.pdf', ins_estado_carga.id, ins_tipo_carga.id
             FROM ins_usuario, ins_estado_carga, ins_tipo_carga
     ),
     ins_pregunta AS (
         INSERT INTO preguntas (texto, respuesta_correcta, tema, id_asignatura)
             SELECT '¿Qué es una variable?', 'Un espacio en memoria para almacenar un valor.', 'Conceptos Básicos', id
             FROM ins_asignatura RETURNING id
     ),
     ins_juego AS (
         INSERT INTO juegos(id_usuario, id_asignatura, puntaje)
             SELECT ins_usuario.id, ins_asignatura.id, 150.00
             FROM ins_usuario, ins_asignatura
     ),
     ins_puntajes AS (
         INSERT INTO puntajes (id_usuario, id_asignatura, puntaje)
             SELECT ins_usuario.id, ins_asignatura.id, 150.00
             FROM ins_usuario, ins_asignatura
     ),
     ins_ranking AS (
         INSERT INTO ranking (id_usuario, id_asignatura, puntaje_total, posicion)
             SELECT ins_usuario.id, ins_asignatura.id, 150.00, 1
             FROM ins_usuario, ins_asignatura
     )
INSERT INTO metricas (id_usuario, id_pregunta, respuesta_correcta, tiempo_respuesta_ms)
SELECT ins_usuario.id, ins_pregunta.id, TRUE, 3500
FROM ins_usuario, ins_pregunta;