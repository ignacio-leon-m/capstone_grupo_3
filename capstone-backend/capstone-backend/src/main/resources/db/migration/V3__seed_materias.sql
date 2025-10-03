-- Crea (si no existe) la carrera base
INSERT INTO carreras (nombre)
VALUES ('Ingeniería Informática')
ON CONFLICT (nombre) DO NOTHING;

-- Programa 4 materias bajo "Ingeniería Informática" evitando duplicados
INSERT INTO materias (nombre, id_carrera)
SELECT 'Programación II', c.id
FROM carreras c
WHERE c.nombre = 'Ingeniería Informática'
  AND NOT EXISTS (SELECT 1 FROM materias m WHERE m.nombre = 'Programación II');

INSERT INTO materias (nombre, id_carrera)
SELECT 'Estructuras de Datos', c.id
FROM carreras c
WHERE c.nombre = 'Ingeniería Informática'
  AND NOT EXISTS (SELECT 1 FROM materias m WHERE m.nombre = 'Estructuras de Datos');

INSERT INTO materias (nombre, id_carrera)
SELECT 'Bases de Datos Avanzadas', c.id
FROM carreras c
WHERE c.nombre = 'Ingeniería Informática'
  AND NOT EXISTS (SELECT 1 FROM materias m WHERE m.nombre = 'Bases de Datos Avanzadas');

INSERT INTO materias (nombre, id_carrera)
SELECT 'Arquitectura de Software', c.id
FROM carreras c
WHERE c.nombre = 'Ingeniería Informática'
  AND NOT EXISTS (SELECT 1 FROM materias m WHERE m.nombre = 'Arquitectura de Software');
