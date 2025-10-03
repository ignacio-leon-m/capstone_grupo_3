-- ===== TEMAS, DOCUMENTOS Y QUIZ =====

-- Dependencias:
--   - materias(id) ya existe
--   - usuarios(id) ya existe (para saber quién creó el tema)
--   - pgcrypto ya está habilitado en V2

-- Elimina si existiera (para evitar conflictos en entornos de prueba)
DROP TABLE IF EXISTS documentos CASCADE;
DROP TABLE IF EXISTS temas CASCADE;

-- ========= TEMAS =========
CREATE TABLE temas (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_materia      UUID NOT NULL REFERENCES materias(id) ON DELETE CASCADE,
    titulo          VARCHAR(150) NOT NULL,
    descripcion     TEXT,
    resumen         TEXT,                   -- aquí guardaremos el resumen generado por IA
    quiz            JSONB,                  -- aquí guardaremos las preguntas del quiz (JSON)
    creado_por      UUID NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    creado_en       TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado_en  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_temas_materia     ON temas(id_materia);
CREATE INDEX idx_temas_creado_por  ON temas(creado_por);

-- ========= DOCUMENTOS =========
CREATE TABLE documentos (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_tema         UUID NOT NULL REFERENCES temas(id) ON DELETE CASCADE,
    file_name       VARCHAR(255) NOT NULL,
    mime_type       VARCHAR(120)  NOT NULL,
    storage_path    VARCHAR(500)  NOT NULL,  -- ruta local o URL en cloud storage
    size_bytes      BIGINT NOT NULL,
    subido_en       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_documentos_tema ON documentos(id_tema);

-- (Opcional) Tema de ejemplo sin documentos (borra si no lo quieres)
-- INSERT INTO temas (id_materia, titulo, descripcion, creado_por)
-- SELECT m.id, 'Tema de ejemplo', 'Descripción del tema', u.id
-- FROM materias m, usuarios u
-- WHERE m.nombre = 'Programación II' AND u.correo = 'admin@demo.cl'
-- LIMIT 1;
