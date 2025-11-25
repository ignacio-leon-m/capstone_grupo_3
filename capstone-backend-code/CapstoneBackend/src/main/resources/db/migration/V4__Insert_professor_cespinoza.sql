-- Insert Professor Cristian Espinoza and assign to subject "Algoritmos y Programación"
-- Password: duoc123 (bcrypt hashed)

DO $$
DECLARE
    v_rol_profesor_id UUID;
    v_carrera_id UUID;
    v_asignatura_id UUID;
    v_profesor_id UUID;
    v_existing_profesor_id UUID;
BEGIN
    -- Get profesor role ID
    SELECT id INTO v_rol_profesor_id 
    FROM roles 
    WHERE LOWER(nombre) = 'profesor' 
    LIMIT 1;

    IF v_rol_profesor_id IS NULL THEN
        RAISE EXCEPTION 'Rol "profesor" not found';
    END IF;

    -- Get carrera ID (Ingeniería Informática)
    SELECT id INTO v_carrera_id 
    FROM carreras 
    WHERE nombre = 'ing-informatica' 
    LIMIT 1;

    IF v_carrera_id IS NULL THEN
        RAISE EXCEPTION 'Carrera "ing-informatica" not found';
    END IF;

    -- Get asignatura ID (Algoritmos y Programación)
    SELECT id INTO v_asignatura_id 
    FROM asignaturas 
    WHERE nombre = 'Algoritmos y Programación' 
    LIMIT 1;

    IF v_asignatura_id IS NULL THEN
    END IF;

    -- Check if professor already exists
    SELECT id INTO v_existing_profesor_id
    FROM usuarios
    WHERE correo = 'cespinoza@duoc.cl';

    IF v_existing_profesor_id IS NULL THEN
        -- Insert professor Cristian Espinoza
        -- Password: duoc123 (BCrypt hash with strength 10)
        INSERT INTO usuarios (
            nombre, 
            apellido, 
            rut, 
            id_rol, 
            id_carrera, 
            password_hash, 
            correo, 
            celular
        ) VALUES (
            'Cristian', 
            'Espinoza', 
            '18765432-1', 
            v_rol_profesor_id, 
            v_carrera_id, 
            '5df12880a2d7356cf0e825d64896b8e1e1fa36d6fbe0cb2632a52f2e84fbbf07', 
            'cespinoza@duoc.cl', 
            '912345678'
        )
        RETURNING id INTO v_profesor_id;
    ELSE
        v_profesor_id := v_existing_profesor_id;
    END IF;

    -- Assign professor to subject (insert into usuario_asignatura)
    INSERT INTO usuario_asignatura (id_usuario, id_asignatura, activo, fecha_asignacion)
    VALUES (v_profesor_id, v_asignatura_id, TRUE, NOW())
    ON CONFLICT (id_usuario, id_asignatura) DO NOTHING;
END $$;
