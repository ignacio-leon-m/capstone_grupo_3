DO $$
DECLARE
    v_carrera_id UUID;
BEGIN
    -- Obtener el ID de la carrera 'ing-informatica' para asociar las nuevas asignaturas.
    -- Esta lógica asume que la carrera ya fue insertada por la migración V2.
    SELECT id INTO v_carrera_id 
    FROM carreras 
    WHERE nombre = 'ing-informatica' 
    LIMIT 1;

    -- Solo proceder si la carrera existe, para evitar errores.
    IF v_carrera_id IS NOT NULL THEN
        RAISE NOTICE 'Insertando asignaturas restantes para la carrera ID: %', v_carrera_id;

        -- Lista de asignaturas a insertar.
        -- La cláusula 'WHERE NOT EXISTS' previene la inserción de duplicados.
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Base de Datos Aplicada I', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Base de Datos Aplicada I' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Base de Datos Aplicada II', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Base de Datos Aplicada II' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Bases de Innovación', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Bases de Innovación' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'BPM Aplicado', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'BPM Aplicado' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Desarrollo Cloud Native I', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Desarrollo Cloud Native I' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Desarrollo Cloud Native II', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Desarrollo Cloud Native II' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Desarrollo de Aplicaciones Móviles', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Desarrollo de Aplicaciones Móviles' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Desarrollo de Soluciones de Software', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Desarrollo de Soluciones de Software' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Desarrollo Fullstack II', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Desarrollo Fullstack II' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Desarrollo Fullstack III', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Desarrollo Fullstack III' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Desarrollo Orientado a Objetos', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Desarrollo Orientado a Objetos' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Estadística Descriptiva', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Estadística Descriptiva' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Ética para el Trabajo', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Ética para el Trabajo' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Ética Profesional', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Ética Profesional' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Fundamentos de Antropología', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Fundamentos de Antropología' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Fundamentos de Programación', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Fundamentos de Programación' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Gestión de Proyectos de Software', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Gestión de Proyectos de Software' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Habilidades de Comunicación', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Habilidades de Comunicación' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Ingeniería de Requisitos', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Ingeniería de Requisitos' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Ingeniería de Software', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Ingeniería de Software' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Inglés Elemental I', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Inglés Elemental I' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Inglés Elemental II', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Inglés Elemental II' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Inglés Intermedio I', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Inglés Intermedio I' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Introducción a Cloud Computing', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Introducción a Cloud Computing' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Introducción a Herramientas DevOps', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Introducción a Herramientas DevOps' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Matemática Aplicada', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Matemática Aplicada' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Nivelación Matemática', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Nivelación Matemática' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Práctica Profesional', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Práctica Profesional' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Seguridad y Calidad en el Desarrollo de Software', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Seguridad y Calidad en el Desarrollo de Software' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Taller Aplicado de Software', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Taller Aplicado de Software' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Taller de Base de Datos', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Taller de Base de Datos' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Taller de Tecnologías de Vanguardia', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Taller de Tecnologías de Vanguardia' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Algoritmos y Programación', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Algoritmos y Programación' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Desarrollo Fullstack I', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Desarrollo Fullstack I' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Arquitecturas TI Contemporáneas', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Arquitecturas TI Contemporáneas' AND id_carrera = v_carrera_id);
        INSERT INTO asignaturas (nombre, id_carrera) SELECT 'Base de Datos', v_carrera_id WHERE NOT EXISTS (SELECT 1 FROM asignaturas WHERE nombre = 'Base de Datos' AND id_carrera = v_carrera_id);

        RAISE NOTICE 'Inserción de asignaturas restantes completada.';
    ELSE
        RAISE WARNING 'No se encontró la carrera ''ing-informatica''. No se insertaron las asignaturas restantes.';
    END IF;
END $$;
