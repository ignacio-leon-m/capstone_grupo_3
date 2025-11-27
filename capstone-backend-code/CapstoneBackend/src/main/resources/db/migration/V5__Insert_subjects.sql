DO $$
DECLARE
    v_id_carrera UUID;
BEGIN
    -- Get the ID of the career 'ing-informatica'
    SELECT id INTO v_id_carrera FROM carreras WHERE nombre = 'ing-informatica' LIMIT 1;

    -- Insert the new subjects
    INSERT INTO asignaturas (nombre, id_carrera) VALUES
        ('Arquitecturas TI Contemporáneas', v_id_carrera),
        ('Base de Datos', v_id_carrera),
        ('Base de Datos Aplicada I', v_id_carrera),
        ('Base de Datos Aplicada II', v_id_carrera),
        ('Bases de Innovación', v_id_carrera),
        ('BPM Aplicado', v_id_carrera),
        ('Desarrollo Cloud Native I', v_id_carrera),
        ('Desarrollo Cloud Native II', v_id_carrera),
        ('Desarrollo de Aplicaciones Móviles', v_id_carrera),
        ('Desarrollo de Soluciones de Software', v_id_carrera),
        ('Desarrollo Fullstack I', v_id_carrera),
        ('Desarrollo Fullstack II', v_id_carrera),
        ('Desarrollo Fullstack III', v_id_carrera),
        ('Desarrollo Orientado a Objetos', v_id_carrera),
        ('Estadística Descriptiva', v_id_carrera),
        ('Ética para el Trabajo', v_id_carrera),
        ('Ética Profesional', v_id_carrera),
        ('Fundamentos de Antropología', v_id_carrera),
        ('Fundamentos de Programación', v_id_carrera),
        ('Gestión de Proyectos de Software', v_id_carrera),
        ('Habilidades de Comunicación', v_id_carrera),
        ('Ingeniería de Requisitos', v_id_carrera),
        ('Ingeniería de Software', v_id_carrera),
        ('Inglés Elemental I', v_id_carrera),
        ('Inglés Elemental II', v_id_carrera),
        ('Inglés Intermedio I', v_id_carrera),
        ('Introducción a Cloud Computing', v_id_carrera),
        ('Introducción a Herramientas DevOps', v_id_carrera),
        ('Matemática Aplicada', v_id_carrera),
        ('Nivelación Matemática', v_id_carrera),
        ('Práctica Profesional', v_id_carrera),
        ('Seguridad y Calidad en el Desarrollo de Software', v_id_carrera),
        ('Taller Aplicado de Software', v_id_carrera),
        ('Taller de Base de Datos', v_id_carrera),
        ('Taller de Tecnologías de Vanguardia', v_id_carrera);
END $$;
