-- Eliminar tablas si existen (en orden inverso de creación para manejar dependencias)
DROP TABLE IF EXISTS metricas_juego_hangman CASCADE;
DROP TABLE IF EXISTS resultados_juego_hangman CASCADE;
DROP TABLE IF EXISTS metricas_juego_crisscross CASCADE;  -- Descomentar cuando se implemente
DROP TABLE IF EXISTS resultados_juego_crisscross CASCADE;  -- Descomentar cuando se implemente
DROP TABLE IF EXISTS metricas CASCADE;
DROP TABLE IF EXISTS puntajes CASCADE;
DROP TABLE IF EXISTS juegos CASCADE;
DROP TABLE IF EXISTS conceptos CASCADE;
DROP TABLE IF EXISTS preguntas CASCADE;
DROP TABLE IF EXISTS temas CASCADE;
DROP TABLE IF EXISTS usuario_asignatura CASCADE;
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

-- Extensión para generar UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--- Tablas de Ubicación e Institución (Sin Cambios)
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
-- Tablas de Roles y Contenido (Sin Cambios)
CREATE TABLE roles (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       nombre VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE carreras (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          nombre VARCHAR(100) NOT NULL,
                          id_institucion UUID NOT NULL,
                          FOREIGN KEY (id_institucion) REFERENCES instituciones(id),
                          UNIQUE (nombre, id_institucion)
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
CREATE TABLE asignaturas_semestre (
                                      id_asignatura UUID NOT NULL,
                                      id_semestre UUID NOT NULL,
                                      PRIMARY KEY (id_asignatura, id_semestre),
                                      FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
                                      FOREIGN KEY (id_semestre) REFERENCES semestres(id)
);
-- Tablas de Usuarios (Sin Cambios)
CREATE TABLE usuarios (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          nombre VARCHAR(100) NOT NULL,
                          apellido VARCHAR(100) NOT NULL,
                          rut VARCHAR (12) NOT NULL UNIQUE,
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

-- Tabla de Relación Usuario-Asignatura (Unificada para Profesores y Alumnos)
-- El rol del usuario determina si es profesor o alumno
CREATE TABLE usuario_asignatura (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_usuario UUID NOT NULL,
    id_asignatura UUID NOT NULL,
    fecha_asignacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id) ON DELETE CASCADE,
    UNIQUE (id_usuario, id_asignatura)
);

-- Tablas de Gamificación
CREATE TABLE temas (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       nombre VARCHAR(100) NOT NULL,
                       id_asignatura UUID NOT NULL,
                       FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
                       UNIQUE (nombre, id_asignatura)
);
CREATE TABLE preguntas (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           texto TEXT NOT NULL,
                           respuesta_correcta TEXT NOT NULL,
                           id_asignatura UUID NOT NULL,
                           id_tema UUID NOT NULL,
                           FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id),
                           FOREIGN KEY (id_tema) REFERENCES temas(id)
);
CREATE TABLE conceptos (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           palabra_concepto VARCHAR(255) NOT NULL,
                           hint TEXT,  -- Pista corta generada por Gemini IA
                           fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           id_tema UUID NOT NULL,
                           FOREIGN KEY (id_tema) REFERENCES temas(id)
);
CREATE TABLE juegos (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        id_usuario UUID NOT NULL,
                        id_asignatura UUID NOT NULL,
                        nombre_juego VARCHAR(50),
                        intentos_restantes INT,
                        estado_partida VARCHAR(50) NOT NULL DEFAULT 'En curso',
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

-- ============================================================
-- TABLAS ESPECÍFICAS POR JUEGO (PATRÓN ESCALABLE)
-- Nomenclatura: metricas_juego_{nombre} + resultados_juego_{nombre}
-- ============================================================

-- JUEGO: HANGMAN (AHORCADO)
CREATE TABLE metricas_juego_hangman (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_usuario UUID NOT NULL,
    id_concepto UUID NOT NULL,
    letra_intentada CHAR(1) NOT NULL,
    es_correcta BOOLEAN NOT NULL,
    posicion_letra INT,  -- Posición en la palabra (0-indexed)
    tiempo_respuesta_ms INT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE
);

CREATE TABLE resultados_juego_hangman (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_concepto UUID NOT NULL,
    adivinado BOOLEAN NOT NULL,  -- TRUE si completó la palabra
    intentos_usados INT NOT NULL,  -- Cuántos errores tuvo en esta palabra
    tiempo_total_ms INT,
    puntaje_obtenido NUMERIC(10, 2),
    vidas_restantes INT,  -- Vidas que quedaban al terminar esta palabra
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE,
    UNIQUE (id_juego, id_concepto)
);

-- JUEGO: CRISS-CROSS PUZZLE (CRUCIGRAMA) - EJEMPLO PARA FUTUROS JUEGOS
-- Descomentar cuando se implemente este juego
/*
CREATE TABLE metricas_juego_crisscross (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_usuario UUID NOT NULL,
    id_concepto UUID NOT NULL,
    posicion_fila INT NOT NULL,
    posicion_columna INT NOT NULL,
    direccion VARCHAR(20),  -- 'HORIZONTAL', 'VERTICAL'
    letra_colocada CHAR(1) NOT NULL,
    es_correcta BOOLEAN NOT NULL,
    tiempo_respuesta_ms INT,
    pista_usada BOOLEAN DEFAULT FALSE,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE
);

CREATE TABLE resultados_juego_crisscross (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_juego UUID NOT NULL,
    id_concepto UUID NOT NULL,
    completado BOOLEAN NOT NULL,
    casillas_correctas INT,
    casillas_totales INT,
    pistas_usadas INT DEFAULT 0,
    tiempo_total_ms INT,
    puntaje_obtenido NUMERIC(10, 2),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_juego) REFERENCES juegos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_concepto) REFERENCES conceptos(id) ON DELETE CASCADE,
    UNIQUE (id_juego, id_concepto)
);
*/

-- INDICES PARA MEJORAR PERFORMANCE

-- Indices para relaciones usuario-asignatura
CREATE INDEX idx_usuario_asignatura_usuario ON usuario_asignatura(id_usuario);
CREATE INDEX idx_usuario_asignatura_asignatura ON usuario_asignatura(id_asignatura);
CREATE INDEX idx_usuario_asignatura_activo ON usuario_asignatura(activo);

-- Indices para conceptos
CREATE INDEX idx_conceptos_tema ON conceptos(id_tema);
CREATE INDEX idx_conceptos_fecha ON conceptos(fecha_creacion DESC);

-- Indices para metricas Hangman
CREATE INDEX idx_metricas_hangman_juego ON metricas_juego_hangman(id_juego);
CREATE INDEX idx_metricas_hangman_usuario ON metricas_juego_hangman(id_usuario);
CREATE INDEX idx_metricas_hangman_concepto ON metricas_juego_hangman(id_concepto);
CREATE INDEX idx_metricas_hangman_fecha ON metricas_juego_hangman(fecha_hora DESC);

-- Indices para resultados Hangman
CREATE INDEX idx_resultados_hangman_juego ON resultados_juego_hangman(id_juego);
CREATE INDEX idx_resultados_hangman_adivinado ON resultados_juego_hangman(adivinado);

-- COMENTARIOS PARA DOCUMENTACION

COMMENT ON TABLE usuario_asignatura IS 'Relación N:M entre usuarios y asignaturas. El rol del usuario determina si es profesor o alumno.';
COMMENT ON TABLE conceptos IS 'Conceptos académicos extraídos automáticamente por IA (Gemini) desde PDFs';
COMMENT ON TABLE metricas_juego_hangman IS 'Métricas granulares del juego Hangman - cada intento de letra';
COMMENT ON TABLE resultados_juego_hangman IS 'Resultados finales del juego Hangman - por palabra completada';

COMMENT ON COLUMN conceptos.hint IS 'Pista corta generada automáticamente por Gemini para juegos de adivinanza';
COMMENT ON COLUMN conceptos.palabra_concepto IS 'Concepto en MAYUSCULAS extraído del PDF académico';
COMMENT ON COLUMN conceptos.fecha_creacion IS 'Timestamp de cuando Gemini procesó y extrajo el concepto';

/**********************INSERCION DE DATOS***********************/

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
         INSERT INTO semestres (nombre) VALUES ('Primer Semestre 2026') RETURNING id
     ),
     ins_carrera AS (
         INSERT INTO carreras (nombre, id_institucion)
             SELECT 'ing-informatica', id FROM ins_institucion RETURNING id
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
         -- CORRECCIÓN: Se agrega 'rut' que es NOT NULL
         INSERT INTO usuarios (nombre, apellido, rut, id_rol, id_carrera, password_hash, correo, celular)
             SELECT 'Cecilia', 'Arroyo', '12345678-9', ins_rol_1.id, ins_carrera.id, '$2a$10$tCQpkcBLaBTmiMzOcdDamOkCxeGc4nIJXzFUkwcWAU8Cj5iRkLa/K',
                    'cecilia.arroyo@duoc.cl', '1234567'
             FROM ins_rol_1, ins_carrera RETURNING id
     ),
     ins_tema AS (
         -- NUEVO CTE: Se inserta el tema para usar su ID en preguntas
         INSERT INTO temas (nombre, id_asignatura)
             SELECT 'Conceptos Básicos', id FROM ins_asignatura RETURNING id
     ),
     ins_pregunta AS (
         -- CORRECCIÓN: Se usa id_tema en lugar de 'tema'
         INSERT INTO preguntas (texto, respuesta_correcta, id_tema, id_asignatura)
             SELECT '¿Qué es una variable?', 'Un espacio en memoria para almacenar un valor.', ins_tema.id, ins_asignatura.id
             FROM ins_asignatura, ins_tema RETURNING id
     ),
    inst_concepto AS (
        INSERT INTO conceptos (palabra_concepto, hint, id_tema)
            SELECT 'VARIABLE', 
                   'Espacio en memoria que almacena un valor', 
                   ins_tema.id 
            FROM ins_tema RETURNING id
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
     )
INSERT INTO metricas (id_usuario, id_pregunta, respuesta_correcta, tiempo_respuesta_ms)
SELECT ins_usuario.id, ins_pregunta.id, TRUE, 3500
FROM ins_usuario, ins_pregunta;