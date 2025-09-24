-- 🔥 ATENCIÓN: esto borra y recrea tablas de dominio (no toca flyway_schema_history)
DROP TABLE IF EXISTS puntajes CASCADE;
DROP TABLE IF EXISTS materias CASCADE;
DROP TABLE IF EXISTS carreras CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- Extensión para UUID (usa pgcrypto que ya vimos disponible)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ===== ROLES =====
CREATE TABLE roles (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre  VARCHAR(50) NOT NULL UNIQUE           -- ← tu entidad Role usa 'nombre'
);

-- ===== USUARIOS =====
CREATE TABLE usuarios (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre            VARCHAR(100) NOT NULL,      -- firstName en entidad
    apellido          VARCHAR(100) NOT NULL,      -- lastName en entidad
    id_rol            UUID NOT NULL REFERENCES roles(id),
    correo            VARCHAR(100) NOT NULL UNIQUE,
    estado            BOOLEAN NOT NULL DEFAULT TRUE,
    celular           VARCHAR(20),
    password_hash     VARCHAR(255) NOT NULL,
    fecha_creacion    TIMESTAMP NOT NULL DEFAULT NOW(),   -- LocalDateTime en entidad
    fecha_ultimo_login TIMESTAMP NULL,
    version           BIGINT DEFAULT 0
);

-- Índices útiles
CREATE INDEX idx_usuarios_id_rol ON usuarios(id_rol);

-- ===== CARRERAS =====
CREATE TABLE carreras (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre  VARCHAR(100) NOT NULL UNIQUE
);

-- ===== MATERIAS (N:1 con carreras) =====
CREATE TABLE materias (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre      VARCHAR(100) NOT NULL,
    id_carrera  UUID NOT NULL REFERENCES carreras(id) ON DELETE CASCADE
);
CREATE INDEX idx_materias_id_carrera ON materias(id_carrera);

-- ===== PUNTAJES (usuario - materia) =====
CREATE TABLE puntajes (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario      UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    id_materia      UUID NOT NULL REFERENCES materias(id) ON DELETE CASCADE,
    puntaje         NUMERIC(10,2) NOT NULL,
    fecha_asignacion TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_puntajes_usuario ON puntajes(id_usuario);
CREATE INDEX idx_puntajes_materia ON puntajes(id_materia);

-- ===== SEED BÁSICO =====
-- Usa nombres de rol compatibles con Spring Security (tu servicio devuelve authority = role.name)
INSERT INTO roles (nombre) VALUES
('ROLE_ADMIN'),
('ROLE_PROFESOR'),
('ROLE_ALUMNO')
ON CONFLICT (nombre) DO NOTHING;

-- Admin (password = Admin123!)
INSERT INTO usuarios (nombre, apellido, id_rol, correo, estado, celular, password_hash)
SELECT 'Admin', 'Sistema', r.id, 'admin@demo.cl', TRUE, NULL,
       '$2a$10$Jq5y8h5Q9q0XN1xN9mWj5e3h8f2Nq6mF1a9x1I6qv2N2s4w1E7N3W'
FROM roles r WHERE r.nombre = 'ROLE_ADMIN'
ON CONFLICT (correo) DO NOTHING;

-- Usuario alumno (password = Secreta123)
INSERT INTO usuarios (nombre, apellido, id_rol, correo, estado, celular, password_hash)
SELECT 'Juan', 'Pérez', r.id, 'juan@demo.cl', TRUE, '+569123',
       '$2a$10$plRWqHKYV.GOZ0rwYLtvnuXv003Xgmvswups05cw4U.WcZJvB6q0G'
FROM roles r WHERE r.nombre = 'ROLE_ALUMNO'
ON CONFLICT (correo) DO NOTHING;

-- Algunas carreras/materias de ejemplo
INSERT INTO carreras (nombre) VALUES ('Ingeniería Informática') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO carreras (nombre) VALUES ('Ciberseguridad')         ON CONFLICT (nombre) DO NOTHING;

INSERT INTO materias (nombre, id_carrera)
SELECT 'Programación I', c.id FROM carreras c WHERE c.nombre='Ingeniería Informática'
ON CONFLICT DO NOTHING;

INSERT INTO materias (nombre, id_carrera)
SELECT 'Seguridad de Redes', c.id FROM carreras c WHERE c.nombre='Ciberseguridad'
ON CONFLICT DO NOTHING;
