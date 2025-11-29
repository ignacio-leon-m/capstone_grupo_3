DO $$
DECLARE
    v_carrera_id UUID;
    v_asignatura_id UUID;
    v_asignatura_nombre TEXT;
    v_temas TEXT[] := ARRAY[
        -- Base de Datos Aplicada I
        'Modelado de Datos Avanzado', 'SQL para Análisis de Datos', 'Procedimientos Almacenados y Triggers', 'Optimización de Consultas', 'Introducción a NoSQL',
        -- Base de Datos Aplicada II
        'Bases de Datos Distribuidas', 'Data Warehousing y ETL', 'Administración y Seguridad de BD', 'Bases de Datos en la Nube', 'Big Data y Ecosistema Hadoop',
        -- Bases de Innovación
        'Fundamentos del Emprendimiento Tecnológico', 'Metodologías Ágiles para la Innovación', 'Design Thinking', 'Modelos de Negocio Digitales', 'Prototipado y Validación de Ideas',
        -- BPM Aplicado
        'Introducción a la Gestión de Procesos de Negocio', 'Modelado de Procesos con BPMN', 'Automatización de Procesos (RPA)', 'Monitoreo y Mejora de Procesos', 'Integración de Sistemas con BPM',
        -- Desarrollo Cloud Native I
        'Introducción a la Nube y Contenedores', 'Orquestación con Kubernetes', 'Arquitectura de Microservicios', 'Patrones de Diseño Cloud Native', 'Service Mesh con Istio',
        -- Desarrollo Cloud Native II
        'Computación Serverless (FaaS)', 'Bases de Datos y Almacenamiento en la Nube', 'CI/CD para Aplicaciones Cloud', 'Observabilidad y Monitoreo en la Nube', 'Seguridad en Entornos Cloud',
        -- Desarrollo de Aplicaciones Móviles
        'Introducción a Android/iOS', 'Diseño de Interfaces Móviles (UI/UX)', 'Consumo de APIs REST', 'Persistencia de Datos Local', 'Publicación en Tiendas de Aplicaciones',
        -- Desarrollo de Soluciones de Software
        'Ciclo de Vida del Desarrollo de Software', 'Patrones de Diseño GoF', 'Principios SOLID', 'Calidad de Software y Pruebas', 'Refactorización y Deuda Técnica',
        -- Desarrollo Fullstack II
        'Frameworks de Frontend (React/Vue/Angular)', 'Gestión de Estado Avanzada', 'APIs RESTful con Frameworks Backend (Spring/Django)', 'Autenticación y Autorización (JWT/OAuth)', 'Pruebas End-to-End',
        -- Desarrollo Fullstack III
        'Despliegue de Aplicaciones Fullstack', 'WebSockets y Comunicación en Tiempo Real', 'GraphQL', 'Renderizado del Lado del Servidor (SSR)', 'Optimización de Performance Web',
        -- Desarrollo Orientado a Objetos
        'Principios de la POO', 'Clases, Objetos y Herencia', 'Polimorfismo y Encapsulamiento', 'Interfaces y Clases Abstractas', 'Diagramas UML',
        -- Estadística Descriptiva
        'Conceptos Básicos de Estadística', 'Medidas de Tendencia Central', 'Medidas de Dispersión', 'Visualización de Datos', 'Distribuciones de Frecuencia',
        -- Ética para el Trabajo
        'Valores y Principios en el Entorno Laboral', 'Responsabilidad Profesional', 'Trabajo en Equipo y Colaboración', 'Resolución de Conflictos', 'Cultura Organizacional',
        -- Ética Profesional
        'Códigos de Ética en TI', 'Privacidad y Protección de Datos', 'Propiedad Intelectual y Software', 'Impacto Social de la Tecnología', 'Dilemas Éticos en IA',
        -- Fundamentos de Antropología
        'Introducción a la Antropología', 'Cultura y Sociedad', 'Evolución Humana', 'Diversidad Cultural', 'Antropología y Tecnología',
        -- Fundamentos de Programación
        'Algoritmos y Pseudocódigo', 'Variables y Tipos de Datos', 'Estructuras de Control (Condicionales y Bucles)', 'Funciones y Procedimientos', 'Estructuras de Datos Básicas',
        -- Gestión de Proyectos de Software
        'Metodologías de Gestión (Cascada vs. Ágil)', 'Planificación y Estimación de Proyectos', 'Gestión de Alcance, Tiempo y Costo', 'Gestión de Riesgos', 'Herramientas de Gestión de Proyectos',
        -- Habilidades de Comunicación
        'Comunicación Oral y Escrita', 'Presentaciones Efectivas', 'Escucha Activa y Feedback', 'Comunicación Interpersonal', 'Comunicación en Equipos de Trabajo',
        -- Ingeniería de Requisitos
        'Introducción a la Ingeniería de Requisitos', 'Elicitación y Análisis de Requisitos', 'Especificación de Requisitos', 'Validación y Gestión de Requisitos', 'Modelado de Requisitos (Casos de Uso)',
        -- Ingeniería de Software
        'Modelos de Proceso de Software', 'Principios de Diseño de Software', 'Arquitectura de Software', 'Verificación y Validación', 'Mantenimiento y Evolución del Software',
        -- Inglés Elemental I
        'Presente Simple y Continuo', 'Vocabulario Básico y Saludos', 'Preguntas con "Wh-"', 'Artículos y Sustantivos', 'Adjetivos y Adverbios Básicos',
        -- Inglés Elemental II
        'Pasado Simple y Continuo', 'Futuro con "will" y "going to"', 'Verbos Modales (can, should)', 'Comparativos y Superlativos', 'Vocabulario de Actividades Cotidianas',
        -- Inglés Intermedio I
        'Presente Perfecto', 'Voz Pasiva', 'Condicionales (Tipo 1 y 2)', 'Gerundios e Infinitivos', 'Phrasal Verbs Comunes',
        -- Introducción a Cloud Computing
        'Conceptos Fundamentales de la Nube', 'Modelos de Servicio (IaaS, PaaS, SaaS)', 'Modelos de Despliegue (Público, Privado, Híbrido)', 'Principales Proveedores (AWS, Azure, GCP)', 'Servicios Cloud Esenciales (Cómputo, Almacenamiento, Redes)',
        -- Introducción a Herramientas DevOps
        'Cultura DevOps y Principios', 'Control de Versiones con Git', 'Integración Continua con Jenkins/GitLab CI', 'Infraestructura como Código (Terraform)', 'Monitoreo y Logging (Prometheus, ELK)',
        -- Matemática Aplicada
        'Lógica y Conjuntos', 'Álgebra Lineal', 'Cálculo Diferencial', 'Cálculo Integral', 'Ecuaciones Diferenciales',
        -- Nivelación Matemática
        'Aritmética y Operaciones Básicas', 'Álgebra Elemental', 'Geometría Básica', 'Funciones y Gráficos', 'Resolución de Ecuaciones',
        -- Práctica Profesional
        'Inserción al Mundo Laboral', 'Elaboración de CV y Entrevistas', 'Desarrollo de Proyecto Práctico', 'Reporte y Presentación de Práctica', 'Ética y Conducta Profesional',
        -- Seguridad y Calidad en el Desarrollo de Software
        'Principios de Seguridad de Software', 'Desarrollo Seguro (SDL)', 'Análisis Estático y Dinámico de Seguridad (SAST/DAST)', 'Modelos de Calidad de Software', 'Normas ISO y Estándares de Calidad',
        -- Taller Aplicado de Software
        'Desarrollo de un Proyecto de Software Completo', 'Integración de Tecnologías', 'Trabajo en Equipo y Colaboración Ágil', 'Documentación Técnica y de Usuario', 'Presentación y Defensa del Proyecto',
        -- Taller de Base de Datos
        'Diseño e Implementación de una BD', 'Programación con T-SQL/PL/SQL', 'Tuning y Performance de BD', 'Backup y Recuperación', 'Proyecto Práctico de BD',
        -- Taller de Tecnologías de Vanguardia
        'Exploración de Tendencias (IA, Blockchain, IoT)', 'Desarrollo de un PoC (Prueba de Concepto)', 'Integración con APIs Modernas', 'Nuevos Paradigmas de Programación', 'Presentación de Investigación Tecnológica'
    ];
    v_asignaturas_nombres TEXT[] := ARRAY[
        'Base de Datos Aplicada I', 'Base de Datos Aplicada II', 'Bases de Innovación', 'BPM Aplicado', 'Desarrollo Cloud Native I', 'Desarrollo Cloud Native II',
        'Desarrollo de Aplicaciones Móviles', 'Desarrollo de Soluciones de Software', 'Desarrollo Fullstack II', 'Desarrollo Fullstack III', 'Desarrollo Orientado a Objetos',
        'Estadística Descriptiva', 'Ética para el Trabajo', 'Ética Profesional', 'Fundamentos de Antropología', 'Fundamentos de Programación',
        'Gestión de Proyectos de Software', 'Habilidades de Comunicación', 'Ingeniería de Requisitos', 'Ingeniería de Software', 'Inglés Elemental I',
        'Inglés Elemental II', 'Inglés Intermedio I', 'Introducción a Cloud Computing', 'Introducción a Herramientas DevOps', 'Matemática Aplicada',
        'Nivelación Matemática', 'Práctica Profesional', 'Seguridad y Calidad en el Desarrollo de Software', 'Taller Aplicado de Software', 'Taller de Base de Datos', 'Taller de Tecnologías de Vanguardia'
    ];
    v_tema_actual TEXT;
    i INTEGER;
    j INTEGER;

BEGIN
    -- Obtener el ID de la carrera 'ing-informatica'
    SELECT id INTO v_carrera_id FROM carreras WHERE nombre = 'ing-informatica' LIMIT 1;

    IF v_carrera_id IS NOT NULL THEN
        RAISE NOTICE 'Iniciando inserción de temas para asignaturas...';
        
        -- Iterar sobre la lista de nombres de asignaturas
        FOR i IN 1..array_length(v_asignaturas_nombres, 1) LOOP
            v_asignatura_nombre := v_asignaturas_nombres[i];
            
            -- Obtener el ID de la asignatura actual
            SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = v_asignatura_nombre AND id_carrera = v_carrera_id LIMIT 1;
            
            IF v_asignatura_id IS NOT NULL THEN
                RAISE NOTICE 'Insertando temas para la asignatura: % (ID: %)', v_asignatura_nombre, v_asignatura_id;
                
                -- Iterar 5 veces para insertar los 5 temas correspondientes
                FOR j IN 1..5 LOOP
                    -- Calcular el índice del tema en el array de temas
                    v_tema_actual := v_temas[(i-1)*5 + j];
                    
                    -- Insertar el tema si no existe para esa asignatura
                    INSERT INTO temas (nombre, id_asignatura)
                    SELECT v_tema_actual, v_asignatura_id
                    WHERE NOT EXISTS (
                        SELECT 1 FROM temas WHERE nombre = v_tema_actual AND id_asignatura = v_asignatura_id
                    );
                END LOOP;
            ELSE
                RAISE WARNING 'No se encontró la asignatura ''%''. No se insertaron sus temas.', v_asignatura_nombre;
            END IF;
        END LOOP;
        
        RAISE NOTICE 'Proceso de inserción de temas completado.';
    ELSE
        RAISE WARNING 'No se encontró la carrera ''ing-informatica''. No se insertaron temas.';
    END IF;
END $$;
