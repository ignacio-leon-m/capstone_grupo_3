-- Inserts base educational content for the subject "Algoritmos y Programación"

-- Get the ID of the subject "Algoritmos y Programación"
DO $$
DECLARE
    v_asignatura_id UUID;
    v_tema_basicos_id UUID;
    v_tema_estructuras_id UUID;
    v_tema_ordenamiento_id UUID;
BEGIN
    -- Buscar la asignatura (creada en V2)
    SELECT id INTO v_asignatura_id 
    FROM asignaturas 
    WHERE nombre = 'Algoritmos y Programación' 
    LIMIT 1;

    IF v_asignatura_id IS NULL THEN
        RAISE EXCEPTION 'Asignatura "Algoritmos y Programación" no encontrada';
    END IF;

    -- ============================================================
    -- INSERTAR TEMAS
    -- ============================================================
    
    -- Tema 1: Conceptos Básicos
    INSERT INTO temas (nombre, id_asignatura)
    VALUES ('Conceptos Básicos de Programación', v_asignatura_id)
    ON CONFLICT (nombre, id_asignatura) DO NOTHING
    RETURNING id INTO v_tema_basicos_id;
    
    IF v_tema_basicos_id IS NULL THEN
        SELECT id INTO v_tema_basicos_id 
        FROM temas 
        WHERE nombre = 'Conceptos Básicos de Programación' 
        AND id_asignatura = v_asignatura_id;
    END IF;

    -- Tema 2: Estructuras de Datos
    INSERT INTO temas (nombre, id_asignatura)
    VALUES ('Estructuras de Datos', v_asignatura_id)
    ON CONFLICT (nombre, id_asignatura) DO NOTHING
    RETURNING id INTO v_tema_estructuras_id;
    
    IF v_tema_estructuras_id IS NULL THEN
        SELECT id INTO v_tema_estructuras_id 
        FROM temas 
        WHERE nombre = 'Estructuras de Datos' 
        AND id_asignatura = v_asignatura_id;
    END IF;

    -- Tema 3: Algoritmos de Ordenamiento
    INSERT INTO temas (nombre, id_asignatura)
    VALUES ('Algoritmos de Ordenamiento', v_asignatura_id)
    ON CONFLICT (nombre, id_asignatura) DO NOTHING
    RETURNING id INTO v_tema_ordenamiento_id;
    
    IF v_tema_ordenamiento_id IS NULL THEN
        SELECT id INTO v_tema_ordenamiento_id 
        FROM temas 
        WHERE nombre = 'Algoritmos de Ordenamiento' 
        AND id_asignatura = v_asignatura_id;
    END IF;

    -- ============================================================
    -- INSERTAR CONCEPTOS para Tema 1: Conceptos Básicos
    -- ============================================================
    
    INSERT INTO conceptos (palabra_concepto, id_tema) VALUES
    ('VARIABLE', v_tema_basicos_id),
    ('FUNCION', v_tema_basicos_id),
    ('BUCLE', v_tema_basicos_id),
    ('CONDICIONAL', v_tema_basicos_id),
    ('ALGORITMO', v_tema_basicos_id),
    ('CONSTANTE', v_tema_basicos_id),
    ('OPERADOR', v_tema_basicos_id),
    ('EXPRESION', v_tema_basicos_id),
    ('ASIGNACION', v_tema_basicos_id),
    ('SINTAXIS', v_tema_basicos_id),
    ('COMENTARIO', v_tema_basicos_id),
    ('COMPILADOR', v_tema_basicos_id),
    ('DEPURACION', v_tema_basicos_id),
    ('PARAMETRO', v_tema_basicos_id),
    ('RETORNO', v_tema_basicos_id)
    ON CONFLICT DO NOTHING;

    -- ============================================================
    -- INSERTAR CONCEPTOS para Tema 2: Estructuras de Datos
    -- ============================================================
    
    INSERT INTO conceptos (palabra_concepto, id_tema) VALUES
    ('ARRAY', v_tema_estructuras_id),
    ('LISTA', v_tema_estructuras_id),
    ('PILA', v_tema_estructuras_id),
    ('COLA', v_tema_estructuras_id),
    ('DICCIONARIO', v_tema_estructuras_id),
    ('INDICE', v_tema_estructuras_id),
    ('NODO', v_tema_estructuras_id),
    ('MATRIZ', v_tema_estructuras_id),
    ('CONJUNTO', v_tema_estructuras_id),
    ('TUPLA', v_tema_estructuras_id),
    ('ITERADOR', v_tema_estructuras_id),
    ('ARBOL', v_tema_estructuras_id),
    ('GRAFO', v_tema_estructuras_id),
    ('ENLAZADA', v_tema_estructuras_id),
    ('ORDENADA', v_tema_estructuras_id)
    ON CONFLICT DO NOTHING;

    -- ============================================================
    -- INSERTAR CONCEPTOS para Tema 3: Algoritmos de Ordenamiento
    -- ============================================================
    
    INSERT INTO conceptos (palabra_concepto, id_tema) VALUES
    ('ORDENAMIENTO', v_tema_ordenamiento_id),
    ('BURBUJA', v_tema_ordenamiento_id),
    ('SELECCION', v_tema_ordenamiento_id),
    ('INSERCION', v_tema_ordenamiento_id),
    ('QUICKSORT', v_tema_ordenamiento_id),
    ('MERGESORT', v_tema_ordenamiento_id),
    ('PIVOTE', v_tema_ordenamiento_id),
    ('COMPLEJIDAD', v_tema_ordenamiento_id),
    ('RECURSION', v_tema_ordenamiento_id),
    ('INTERCAMBIO', v_tema_ordenamiento_id),
    ('COMPARACION', v_tema_ordenamiento_id),
    ('ESTABLE', v_tema_ordenamiento_id),
    ('PARTICION', v_tema_ordenamiento_id),
    ('FUSION', v_tema_ordenamiento_id),
    ('ITERACION', v_tema_ordenamiento_id)
    ON CONFLICT DO NOTHING;

    -- ============================================================
    -- INSERTAR PREGUNTAS para Tema 1: Conceptos Básicos
    -- ============================================================
    
    INSERT INTO preguntas (texto, respuesta_correcta, id_asignatura, id_tema) VALUES
    ('¿Qué es una variable en programación?', 
     'Un espacio en memoria para almacenar un valor que puede cambiar durante la ejecución del programa',
     v_asignatura_id, v_tema_basicos_id),
    
    ('¿Para qué sirve un bucle while en programación?',
     'Para repetir un bloque de código mientras se cumpla una condición',
     v_asignatura_id, v_tema_basicos_id),
    
    ('¿Qué estructura de control se utiliza para tomar decisiones en un programa?',
     'if-else',
     v_asignatura_id, v_tema_basicos_id),
    
    ('¿Qué es un algoritmo?',
     'Una secuencia finita de pasos bien definidos que resuelven un problema',
     v_asignatura_id, v_tema_basicos_id),
    
    ('¿Cuál es la diferencia entre una variable y una constante?',
     'Una variable puede cambiar su valor durante la ejecución, una constante no',
     v_asignatura_id, v_tema_basicos_id),
    
    ('¿Qué es una función en programación?',
     'Un bloque de código reutilizable que realiza una tarea específica',
     v_asignatura_id, v_tema_basicos_id),
    
    ('¿Para qué sirven los comentarios en el código?',
     'Para documentar y explicar el código sin afectar su ejecución',
     v_asignatura_id, v_tema_basicos_id),
    
    ('¿Qué es la depuración de código?',
     'El proceso de identificar y corregir errores en el programa',
     v_asignatura_id, v_tema_basicos_id)
    ON CONFLICT DO NOTHING;

    -- ============================================================
    -- INSERTAR PREGUNTAS para Tema 2: Estructuras de Datos
    -- ============================================================
    
    INSERT INTO preguntas (texto, respuesta_correcta, id_asignatura, id_tema) VALUES
    ('¿Qué es un array?',
     'Una estructura de datos que almacena elementos del mismo tipo en posiciones consecutivas de memoria',
     v_asignatura_id, v_tema_estructuras_id),
    
    ('¿Cuál es la principal diferencia entre un array y una lista?',
     'El array tiene tamaño fijo, la lista puede crecer o reducirse dinámicamente',
     v_asignatura_id, v_tema_estructuras_id),
    
    ('¿Qué significa LIFO en estructuras de datos?',
     'Last In First Out',
     v_asignatura_id, v_tema_estructuras_id),
    
    ('¿Qué estructura de datos usa el principio FIFO?',
     'Cola',
     v_asignatura_id, v_tema_estructuras_id),
    
    ('¿Qué es un diccionario en programación?',
     'Una estructura que almacena pares clave-valor para búsqueda rápida',
     v_asignatura_id, v_tema_estructuras_id),
    
    ('¿Cómo se accede a un elemento en un array?',
     'Mediante su índice numérico',
     v_asignatura_id, v_tema_estructuras_id),
    
    ('¿Qué es un nodo en estructuras de datos?',
     'Un elemento que contiene un dato y referencias a otros nodos',
     v_asignatura_id, v_tema_estructuras_id),
    
    ('¿Qué es una matriz?',
     'Un array bidimensional organizado en filas y columnas',
     v_asignatura_id, v_tema_estructuras_id)
    ON CONFLICT DO NOTHING;

    -- ============================================================
    -- INSERTAR PREGUNTAS para Tema 3: Algoritmos de Ordenamiento
    -- ============================================================
    
    INSERT INTO preguntas (texto, respuesta_correcta, id_asignatura, id_tema) VALUES
    ('¿Qué es un algoritmo de ordenamiento?',
     'Un proceso que reorganiza elementos de una colección siguiendo un criterio específico de orden',
     v_asignatura_id, v_tema_ordenamiento_id),
    
    ('¿Cómo funciona el algoritmo de ordenamiento burbuja?',
     'Compara elementos adyacentes y los intercambia si están desordenados, repitiendo el proceso',
     v_asignatura_id, v_tema_ordenamiento_id),
    
    ('¿Qué técnica usa MergeSort para ordenar?',
     'Divide y conquista',
     v_asignatura_id, v_tema_ordenamiento_id),
    
    ('¿Qué es un pivote en el algoritmo QuickSort?',
     'Un elemento de referencia usado para dividir la lista en dos particiones',
     v_asignatura_id, v_tema_ordenamiento_id),
    
    ('¿Qué significa que un algoritmo de ordenamiento sea estable?',
     'Mantiene el orden relativo de elementos con valores iguales',
     v_asignatura_id, v_tema_ordenamiento_id),
    
    ('¿Qué operación básica realiza el ordenamiento por selección?',
     'Busca el elemento mínimo y lo coloca en su posición correcta',
     v_asignatura_id, v_tema_ordenamiento_id),
    
    ('¿Qué es la complejidad temporal de un algoritmo?',
     'Una medida del tiempo que requiere en función del tamaño de entrada',
     v_asignatura_id, v_tema_ordenamiento_id)
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Contenido educativo insertado exitosamente para Algoritmos y Programación';
    RAISE NOTICE 'Temas: 3, Conceptos: 45, Preguntas: 23';

END $$;
