-- INSERCION DE DATOS INICIALES

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
         INSERT INTO usuarios (nombre, apellido, rut, id_rol, id_carrera, password_hash, correo, celular)
             SELECT 'Cecilia', 'Arroyo', '12345678-9', ins_rol_1.id, ins_carrera.id, '$2a$10$tCQpkcBLaBTmiMzOcdDamOkCxeGc4nIJXzFUkwcWAU8Cj5iRkLa/K',
                    'cecilia.arroyo@duoc.cl', '1234567'
             FROM ins_rol_1, ins_carrera RETURNING id
     ),
     ins_tema AS (
         INSERT INTO temas (nombre, id_asignatura)
             SELECT 'Conceptos Básicos', id FROM ins_asignatura RETURNING id
     ),
     ins_pregunta AS (
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
