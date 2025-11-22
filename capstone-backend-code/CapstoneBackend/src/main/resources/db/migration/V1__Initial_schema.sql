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
