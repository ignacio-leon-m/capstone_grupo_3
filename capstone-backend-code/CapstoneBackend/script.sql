--
-- 1. DROP DE TABLAS (ORDEN INVERSO POR DEPENDENCIAS)
--

-- Tablas de Gamificación y Auditoría (Dependientes)
DROP TABLE IF EXISTS metricas;
DROP TABLE IF EXISTS ranking;
DROP TABLE IF EXISTS puntajes;
DROP TABLE IF EXISTS juegos;
DROP TABLE IF EXISTS preguntas;
DROP TABLE IF EXISTS cargas;
DROP TABLE IF EXISTS tipos_carga;
DROP TABLE IF EXISTS estados_carga;

-- Tablas de Usuarios y Contenido Intermedio
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS asignaturas_semestre;
DROP TABLE IF EXISTS asignaturas;
DROP TABLE IF EXISTS semestres;
DROP TABLE IF EXISTS carreras;
DROP TABLE IF EXISTS roles;

-- Tablas de Ubicación e Institución (Menos Dependientes)
DROP TABLE IF EXISTS instituciones;
DROP TABLE IF EXISTS comunas;
DROP TABLE IF EXISTS regiones;
DROP TABLE IF EXISTS paises;


--
-- 2. CREACIÓN DE ESTRUCTURA (ORDEN ORIGINAL)
--

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

--- Tablas de Roles y Contenido
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

---
--- Tablas de Usuarios
---
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

---
--- Tablas de Carga y Auditoría
---
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

---
--- Tablas de Gamificación
---
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