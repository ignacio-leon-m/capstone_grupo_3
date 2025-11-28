-- INSERCION DE TODOS LOS DATOS (CONSOLIDADO, IDEMPOTENTE Y COMPATIBLE)
DO $$
DECLARE
    -- IDs para V2
    v_pais_id UUID;
    v_region_id UUID;
    v_comuna_id UUID;
    v_institucion_id UUID;
    v_rol_admin_id UUID;
    v_rol_profesor_id UUID;
    v_rol_alumno_id UUID;
    v_semestre_id UUID;
    v_carrera_id UUID;
    v_usuario_id UUID;
    
    -- IDs para V3, V4 y V6
    v_algoritmos_id UUID;
    v_db_id UUID;
    v_fullstack_id UUID;

    v_tema_id UUID;
    v_pregunta_id UUID;
    v_concepto_id UUID;
    v_profesor_id UUID;
BEGIN
    RAISE NOTICE 'Iniciando inserción de datos consolidados...';

    -- ============================================================
    -- LÓGICA DE DATOS FUNDACIONALES
    -- ============================================================
    RAISE NOTICE 'Insertando datos fundacionales (locaciones, roles, carrera base)...';

    -- Pais
    SELECT id INTO v_pais_id FROM paises WHERE nombre = 'Chile' LIMIT 1;
    IF NOT FOUND THEN
        INSERT INTO paises (nombre) VALUES ('Chile') RETURNING id INTO v_pais_id;
    END IF;

    -- Region
    SELECT id INTO v_region_id FROM regiones WHERE nombre = 'Metropolitana' LIMIT 1;
    IF NOT FOUND THEN
        INSERT INTO regiones (nombre, id_pais) VALUES ('Metropolitana', v_pais_id) RETURNING id INTO v_region_id;
    END IF;

    -- Comuna
    SELECT id INTO v_comuna_id FROM comunas WHERE nombre = 'Cerrillos' LIMIT 1;
    IF NOT FOUND THEN
        INSERT INTO comunas (nombre, id_region) VALUES ('Cerrillos', v_region_id) RETURNING id INTO v_comuna_id;
    END IF;

    -- Institucion
    SELECT id INTO v_institucion_id FROM instituciones WHERE nombre = 'DUOC UC Sede Plaza Oeste' LIMIT 1;
    IF NOT FOUND THEN
        INSERT INTO instituciones (nombre, id_comuna) VALUES ('DUOC UC Sede Plaza Oeste', v_comuna_id) RETURNING id INTO v_institucion_id;
    END IF;

    -- Roles
    SELECT id INTO v_rol_admin_id FROM roles WHERE nombre = 'admin' LIMIT 1;
    IF NOT FOUND THEN INSERT INTO roles(nombre) VALUES ('admin') RETURNING id INTO v_rol_admin_id; END IF;
    SELECT id INTO v_rol_profesor_id FROM roles WHERE nombre = 'profesor' LIMIT 1;
    IF NOT FOUND THEN INSERT INTO roles(nombre) VALUES ('profesor') RETURNING id INTO v_rol_profesor_id; END IF;
    SELECT id INTO v_rol_alumno_id FROM roles WHERE nombre = 'alumno' LIMIT 1;
    IF NOT FOUND THEN INSERT INTO roles(nombre) VALUES ('alumno') RETURNING id INTO v_rol_alumno_id; END IF;

    -- Semestre
    SELECT id INTO v_semestre_id FROM semestres WHERE nombre = 'Primer Semestre 2026' LIMIT 1;
    IF NOT FOUND THEN
        INSERT INTO semestres (nombre) VALUES ('Primer Semestre 2026') RETURNING id INTO v_semestre_id;
    END IF;

    -- Carrera
    SELECT id INTO v_carrera_id FROM carreras WHERE nombre = 'ing-informatica' AND id_institucion = v_institucion_id LIMIT 1;
    IF NOT FOUND THEN
        INSERT INTO carreras (nombre, id_institucion) VALUES ('ing-informatica', v_institucion_id) RETURNING id INTO v_carrera_id;
    END IF;
    
    -- ============================================================
    -- LÓGICA DE ASIGNATURAS Y TEMAS
    -- ============================================================
    RAISE NOTICE 'Insertando asignaturas y temas...';

    -- Asignatura Base: Algoritmos y Programación
    SELECT id INTO v_algoritmos_id FROM asignaturas WHERE nombre = 'Algoritmos y Programación' AND id_carrera = v_carrera_id;
    IF NOT FOUND THEN
        INSERT INTO asignaturas (nombre, id_carrera) VALUES ('Algoritmos y Programación', v_carrera_id) RETURNING id INTO v_algoritmos_id;
    END IF;
    INSERT INTO asignaturas_semestre (id_asignatura, id_semestre) VALUES (v_algoritmos_id, v_semestre_id) ON CONFLICT DO NOTHING;

    -- Resto de asignaturas
    INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Arquitecturas TI Contemporáneas', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Arquitecturas TI Contemporáneas' AND id_carrera = v_carrera_id);
    INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Base de Datos', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Base de Datos' AND id_carrera = v_carrera_id);
    INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Desarrollo Fullstack I', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Desarrollo Fullstack I' AND id_carrera = v_carrera_id);
    -- (se pueden añadir el resto de asignaturas de la misma forma)

    -- Temas para Algoritmos y Programación
    IF v_algoritmos_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) SELECT 'Conceptos Básicos de Programación', v_algoritmos_id WHERE NOT EXISTS (SELECT 1 FROM temas WHERE nombre = 'Conceptos Básicos de Programación' AND id_asignatura = v_algoritmos_id);
        INSERT INTO temas (nombre, id_asignatura) SELECT 'Estructuras de Datos', v_algoritmos_id WHERE NOT EXISTS (SELECT 1 FROM temas WHERE nombre = 'Estructuras de Datos' AND id_asignatura = v_algoritmos_id);
        INSERT INTO temas (nombre, id_asignatura) SELECT 'Algoritmos de Ordenamiento', v_algoritmos_id WHERE NOT EXISTS (SELECT 1 FROM temas WHERE nombre = 'Algoritmos de Ordenamiento' AND id_asignatura = v_algoritmos_id);
    END IF;

    -- Temas para Base de Datos
    SELECT id INTO v_db_id FROM asignaturas WHERE nombre = 'Base de Datos' AND id_carrera = v_carrera_id;
    IF v_db_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) SELECT 'Modelo Entidad-Relación', v_db_id WHERE NOT EXISTS (SELECT 1 FROM temas WHERE nombre = 'Modelo Entidad-Relación' AND id_asignatura = v_db_id);
        INSERT INTO temas (nombre, id_asignatura) SELECT 'Lenguaje de Consulta Estructurado (SQL)', v_db_id WHERE NOT EXISTS (SELECT 1 FROM temas WHERE nombre = 'Lenguaje de Consulta Estructurado (SQL)' AND id_asignatura = v_db_id);
    END IF;

    -- Temas para Desarrollo Fullstack I
    SELECT id INTO v_fullstack_id FROM asignaturas WHERE nombre = 'Desarrollo Fullstack I' AND id_carrera = v_carrera_id;
    IF v_fullstack_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) SELECT 'Desarrollo Frontend con HTML, CSS y JavaScript', v_fullstack_id WHERE NOT EXISTS (SELECT 1 FROM temas WHERE nombre = 'Desarrollo Frontend con HTML, CSS y JavaScript' AND id_asignatura = v_fullstack_id);
        INSERT INTO temas (nombre, id_asignatura) SELECT 'Desarrollo Backend con Node.js y Express', v_fullstack_id WHERE NOT EXISTS (SELECT 1 FROM temas WHERE nombre = 'Desarrollo Backend con Node.js y Express' AND id_asignatura = v_fullstack_id);
    END IF;

    -- ============================================================
    -- LÓGICA DE USUARIOS
    -- ============================================================
    RAISE NOTICE 'Insertando usuarios...';

    -- Usuario Admin
    SELECT id INTO v_usuario_id FROM usuarios WHERE correo = 'cecilia.arroyo@duoc.cl' LIMIT 1;
    IF NOT FOUND THEN
        INSERT INTO usuarios (nombre, apellido, rut, id_rol, id_carrera, password_hash, correo, celular)
        VALUES ('Cecilia', 'Arroyo', '12345678-9', v_rol_admin_id, v_carrera_id, '$2a$10$tCQpkcBLaBTmiMzOcdDamOkCxeGc4nIJXzFUkwcWAU8Cj5iRkLa/K', 'cecilia.arroyo@duoc.cl', '1234567');
    END IF;

    -- Usuario Profesor
    SELECT id INTO v_profesor_id FROM usuarios WHERE correo = 'cespinoza@duoc.cl' LIMIT 1;
    IF NOT FOUND THEN
        INSERT INTO usuarios (nombre, apellido, rut, id_rol, id_carrera, password_hash, correo, celular) 
        VALUES ('Cristian', 'Espinoza', '18765432-1', v_rol_profesor_id, v_carrera_id, '$2a$10$C9O/iE..iP98j2/1s5.OFe4p0cfr2esA/3q5/OTc/x2N39m8p5dI.', 'cespinoza@duoc.cl', '912345678')
        RETURNING id INTO v_profesor_id;
    END IF;
    
    -- Asignar profesor a asignatura
    IF v_profesor_id IS NOT NULL AND v_algoritmos_id IS NOT NULL THEN
        INSERT INTO usuario_asignatura (id_usuario, id_asignatura, activo) VALUES (v_profesor_id, v_algoritmos_id, TRUE) ON CONFLICT DO NOTHING;
    END IF;

    RAISE NOTICE 'Proceso de inserción de datos consolidado completado.';
END $$;
