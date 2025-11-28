DO $$
DECLARE
    v_asignatura_id UUID;
BEGIN
    -- Arquitecturas TI Contemporáneas
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Arquitecturas TI Contemporáneas' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Principios de Arquitectura de Software', v_asignatura_id),
            ('Patrones de Diseño Arquitectónico', v_asignatura_id),
            ('Arquitecturas Orientadas a Servicios (SOA)', v_asignatura_id),
            ('Microservicios y Contenedores', v_asignatura_id),
            ('Arquitecturas Serverless', v_asignatura_id),
            ('Tópicos Avanzados en Arquitecturas Modernas', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Base de Datos
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Base de Datos' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Modelo Entidad-Relación', v_asignatura_id),
            ('Diseño de Bases de Datos Relacionales', v_asignatura_id),
            ('Lenguaje de Consulta Estructurado (SQL)', v_asignatura_id),
            ('Normalización de Bases de Datos', v_asignatura_id),
            ('Transacciones y Concurrencia', v_asignatura_id),
            ('Introducción a Bases de Datos NoSQL', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Base de Datos Aplicada I
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Base de Datos Aplicada I' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Procedimientos Almacenados y Funciones', v_asignatura_id),
            ('Triggers y Vistas', v_asignatura_id),
            ('Optimización de Consultas SQL', v_asignatura_id),
            ('Administración de Usuarios y Seguridad', v_asignatura_id),
            ('Respaldo y Recuperación de Datos', v_asignatura_id),
            ('Conexión de Aplicaciones a Bases de Datos', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Base de Datos Aplicada II
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Base de Datos Aplicada II' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Inteligencia de Negocios (BI) y Data Warehousing', v_asignatura_id),
            ('Modelado Dimensional', v_asignatura_id),
            ('Procesos de Extracción, Transformación y Carga (ETL)', v_asignatura_id),
            ('Herramientas de Visualización de Datos', v_asignatura_id),
            ('Minería de Datos e Introducción a Big Data', v_asignatura_id),
            ('Taller de Proyectos de BI', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Bases de Innovación
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Bases de Innovación' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Creatividad y Generación de Ideas', v_asignatura_id),
            ('Metodologías de Innovación (Design Thinking, Lean Startup)', v_asignatura_id),
            ('Modelos de Negocio y Propuesta de Valor', v_asignatura_id),
            ('Prototipado y Validación de Soluciones', v_asignatura_id),
            ('Estrategias de Emprendimiento', v_asignatura_id),
            ('Presentaciones Efectivas y Pitch', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- BPM Aplicado
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'BPM Aplicado' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Introducción a la Gestión de Procesos de Negocio (BPM)', v_asignatura_id),
            ('Modelado y Notación de Procesos (BPMN)', v_asignatura_id),
            ('Análisis y Mejora de Procesos', v_asignatura_id),
            ('Automatización de Procesos con BPMS', v_asignatura_id),
            ('Monitoreo y Control de Procesos', v_asignatura_id),
            ('Taller de Implementación de BPM', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Desarrollo Cloud Native I
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Desarrollo Cloud Native I' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Introducción a Cloud Native y Contenedores', v_asignatura_id),
            ('Orquestación con Kubernetes', v_asignatura_id),
            ('Desarrollo de Microservicios', v_asignatura_id),
            ('Comunicación entre Microservicios', v_asignatura_id),
            ('Gestión de Configuración y Secrets', v_asignatura_id),
            ('Despliegue Continuo en Kubernetes', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Desarrollo Cloud Native II
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Desarrollo Cloud Native II' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Patrones de Resiliencia y Tolerancia a Fallos', v_asignatura_id),
            ('Observabilidad: Monitoreo, Logging y Tracing', v_asignatura_id),
            ('Service Mesh (e.g., Istio)', v_asignatura_id),
            ('Arquitecturas Orientadas a Eventos', v_asignatura_id),
            ('Seguridad en Entornos Cloud Native', v_asignatura_id),
            ('Optimización de Recursos en Kubernetes', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Desarrollo de Aplicaciones Móviles
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Desarrollo de Aplicaciones Móviles' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Introducción al Desarrollo Móvil (iOS y Android)', v_asignatura_id),
            ('UI/UX para Aplicaciones Móviles', v_asignatura_id),
            ('Gestión de Ciclo de Vida y Componentes', v_asignatura_id),
            ('Consumo de APIs y Almacenamiento Local', v_asignatura_id),
            ('Notificaciones Push y Servicios en Segundo Plano', v_asignatura_id),
            ('Publicación en Tiendas de Aplicaciones', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Desarrollo de Soluciones de Software
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Desarrollo de Soluciones de Software' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Análisis y Diseño de Soluciones', v_asignatura_id),
            ('Selección de Tecnologías y Arquitecturas', v_asignatura_id),
            ('Implementación y Pruebas de Software', v_asignatura_id),
            ('Integración de Sistemas', v_asignatura_id),
            ('Despliegue y Mantenimiento', v_asignatura_id),
            ('Gestión de la Configuración y Versionado', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Desarrollo Fullstack I
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Desarrollo Fullstack I' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Desarrollo Frontend con HTML, CSS y JavaScript', v_asignatura_id),
            ('Frameworks de JavaScript (e.g., React, Vue)', v_asignatura_id),
            ('Desarrollo Backend con Node.js y Express', v_asignatura_id),
            ('Creación de APIs RESTful', v_asignatura_id),
            ('Interacción Frontend-Backend', v_asignatura_id),
            ('Control de Versiones con Git', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Desarrollo Fullstack II
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Desarrollo Fullstack II' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Bases de Datos para Aplicaciones Web', v_asignatura_id),
            ('Autenticación y Autorización de Usuarios', v_asignatura_id),
            ('Pruebas Unitarias y de Integración', v_asignatura_id),
            ('Despliegue de Aplicaciones Web', v_asignatura_id),
            ('WebSockets y Comunicación en Tiempo Real', v_asignatura_id),
            ('Taller de Proyecto Fullstack', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Desarrollo Fullstack III
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Desarrollo Fullstack III' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Optimización y Rendimiento de Aplicaciones Web', v_asignatura_id),
            ('Seguridad en Aplicaciones Web (OWASP Top 10)', v_asignatura_id),
            ('Integración Continua y Despliegue Continuo (CI/CD)', v_asignatura_id),
            ('Arquitecturas Escalables y de Alta Disponibilidad', v_asignatura_id),
            ('Introducción a DevOps y Cultura de Colaboración', v_asignatura_id),
            ('Proyecto Final de Desarrollo Fullstack', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Desarrollo Orientado a Objetos
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Desarrollo Orientado a Objetos' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Principios de la Programación Orientada a Objetos', v_asignatura_id),
            ('Clases, Objetos y Encapsulamiento', v_asignatura_id),
            ('Herencia y Polimorfismo', v_asignatura_id),
            ('Abstracción e Interfaces', v_asignatura_id),
            ('Patrones de Diseño Orientados a Objetos', v_asignatura_id),
            ('Taller de Programación Orientada a Objetos', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Estadística Descriptiva
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Estadística Descriptiva' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Introducción a la Estadística y Tipos de Variables', v_asignatura_id),
            ('Tablas de Frecuencia y Gráficos Estadísticos', v_asignatura_id),
            ('Medidas de Tendencia Central (Media, Mediana, Moda)', v_asignatura_id),
            ('Medidas de Dispersión (Varianza, Desviación Estándar)', v_asignatura_id),
            ('Medidas de Posición (Cuartiles, Percentiles)', v_asignatura_id),
            ('Análisis Exploratorio de Datos', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Ética para el Trabajo
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Ética para el Trabajo' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Concepto y Sentido del Trabajo', v_asignatura_id),
            ('Derechos y Deberes Laborales', v_asignatura_id),
            ('Responsabilidad Social y Empresarial', v_asignatura_id),
            ('Resolución de Conflictos en el Entorno Laboral', v_asignatura_id),
            ('Seguridad y Salud Ocupacional', v_asignatura_id),
            ('Desarrollo Profesional y Proyecto de Vida', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Ética Profesional
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Ética Profesional' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Fundamentos de la Ética y la Moral', v_asignatura_id),
            ('Códigos Deontológicos en Informática', v_asignatura_id),
            ('Dilemas Éticos en la Tecnología', v_asignatura_id),
            ('Privacidad y Protección de Datos', v_asignatura_id),
            ('Propiedad Intelectual y Software', v_asignatura_id),
            ('Impacto Social de la Tecnología', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Fundamentos de Antropología
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Fundamentos de Antropología' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Introducción a la Antropología', v_asignatura_id),
            ('El Ser Humano como Ser Cultural', v_asignatura_id),
            ('Familia y Parentesco', v_asignatura_id),
            ('Religión y Sistemas de Creencias', v_asignatura_id),
            ('Globalización y Diversidad Cultural', v_asignatura_id),
            ('Antropología y Desafíos Contemporáneos', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Fundamentos de Programación
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Fundamentos de Programación' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Introducción al Pensamiento Computacional', v_asignatura_id),
            ('Variables, Tipos de Datos y Operadores', v_asignatura_id),
            ('Estructuras de Control Condicionales', v_asignatura_id),
            ('Estructuras de Control Repetitivas', v_asignatura_id),
            ('Funciones y Modularidad', v_asignatura_id),
            ('Introducción a Estructuras de Datos (Arrays)', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Gestión de Proyectos de Software
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Gestión de Proyectos de Software' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Ciclo de Vida del Desarrollo de Software', v_asignatura_id),
            ('Metodologías Tradicionales (Cascada)', v_asignatura_id),
            ('Metodologías Ágiles (Scrum, Kanban)', v_asignatura_id),
            ('Planificación y Estimación de Proyectos', v_asignatura_id),
            ('Gestión de Riesgos y Calidad', v_asignatura_id),
            ('Herramientas de Gestión de Proyectos', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Habilidades de Comunicación
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Habilidades de Comunicación' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Comunicación Verbal y No Verbal', v_asignatura_id),
            ('Escucha Activa y Empatía', v_asignatura_id),
            ('Presentaciones Orales Efectivas', v_asignatura_id),
            ('Redacción de Informes y Documentos Técnicos', v_asignatura_id),
            ('Comunicación en Equipos de Trabajo', v_asignatura_id),
            ('Negociación y Resolución de Conflictos', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Ingeniería de Requisitos
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Ingeniería de Requisitos' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Introducción a la Ingeniería de Requisitos', v_asignatura_id),
            ('Elicitación y Captura de Requisitos', v_asignatura_id),
            ('Análisis y Modelado de Requisitos (UML)', v_asignatura_id),
            ('Especificación de Requisitos de Software (SRS)', v_asignatura_id),
            ('Validación y Verificación de Requisitos', v_asignatura_id),
            ('Gestión de Cambios en los Requisitos', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Ingeniería de Software
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Ingeniería de Software' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Procesos de Desarrollo de Software', v_asignatura_id),
            ('Diseño Arquitectónico y Detallado', v_asignatura_id),
            ('Calidad de Software y Métricas', v_asignatura_id),
            ('Pruebas de Software (Testing)', v_asignatura_id),
            ('Mantenimiento y Evolución del Software', v_asignatura_id),
            ('Configuración y Gestión de la Configuración', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Inglés Elemental I
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Inglés Elemental I' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Saludos y Presentaciones', v_asignatura_id),
            ('Verbo To Be y Presente Simple', v_asignatura_id),
            ('Vocabulario de la Vida Cotidiana', v_asignatura_id),
            ('Formulación de Preguntas Básicas', v_asignatura_id),
            ('Descripciones de Personas y Lugares', v_asignatura_id),
            ('Comprensión de Textos Sencillos', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Inglés Elemental II
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Inglés Elemental II' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Pasado Simple y Verbos Irregulares', v_asignatura_id),
            ('Expresiones de Tiempo y Frecuencia', v_asignatura_id),
            ('Vocabulario de Viajes y Comida', v_asignatura_id),
            ('Comparativos y Superlativos', v_asignatura_id),
            ('Conversaciones sobre Experiencias Pasadas', v_asignatura_id),
            ('Lectura de Artículos y Relatos Cortos', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Inglés Intermedio I
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Inglés Intermedio I' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Presente Perfecto y Continuo', v_asignatura_id),
            ('Futuro con Will y Going To', v_asignatura_id),
            ('Vocabulario Técnico y de Negocios', v_asignatura_id),
            ('Participación en Reuniones y Debates', v_asignatura_id),
            ('Redacción de Correos Electrónicos Profesionales', v_asignatura_id),
            ('Comprensión Auditiva de Conferencias y Noticias', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Introducción a Cloud Computing
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Introducción a Cloud Computing' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Modelos de Servicio (IaaS, PaaS, SaaS)', v_asignatura_id),
            ('Modelos de Despliegue (Pública, Privada, Híbrida)', v_asignatura_id),
            ('Principales Proveedores de Cloud (AWS, Azure, GCP)', v_asignatura_id),
            ('Servicios de Cómputo y Almacenamiento en la Nube', v_asignatura_id),
            ('Redes y Seguridad en la Nube', v_asignatura_id),
            ('Costos y Optimización en la Nube', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Introducción a Herramientas DevOps
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Introducción a Herramientas DevOps' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Cultura DevOps y Principios', v_asignatura_id),
            ('Control de Versiones Distribuido con Git y GitHub', v_asignatura_id),
            ('Integración Continua con Jenkins o GitLab CI', v_asignatura_id),
            ('Contenedores con Docker', v_asignatura_id),
            ('Orquestación de Contenedores con Kubernetes', v_asignatura_id),
            ('Infraestructura como Código (IaC) con Terraform', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Matemática Aplicada
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Matemática Aplicada' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Lógica Proposicional y de Predicados', v_asignatura_id),
            ('Teoría de Conjuntos y Relaciones', v_asignatura_id),
            ('Funciones y Sucesiones', v_asignatura_id),
            ('Matrices y Sistemas de Ecuaciones Lineales', v_asignatura_id),
            ('Grafos y Árboles', v_asignatura_id),
            ('Aplicaciones en Ciencias de la Computación', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Nivelación Matemática
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Nivelación Matemática' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Aritmética y Operaciones Básicas', v_asignatura_id),
            ('Álgebra Elemental y Expresiones Algebraicas', v_asignatura_id),
            ('Ecuaciones de Primer y Segundo Grado', v_asignatura_id),
            ('Geometría Plana y Trigonometría Básica', v_asignatura_id),
            ('Funciones Lineales y Cuadráticas', v_asignatura_id),
            ('Resolución de Problemas Matemáticos', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Práctica Profesional
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Práctica Profesional' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Inserción en el Mundo Laboral', v_asignatura_id),
            ('Aplicación de Conocimientos Técnicos en un Entorno Real', v_asignatura_id),
            ('Desarrollo de Habilidades Blandas', v_asignatura_id),
            ('Elaboración de Informes de Práctica', v_asignatura_id),
            ('Gestión del Tiempo y Responsabilidades', v_asignatura_id),
            ('Evaluación de Desempeño y Retroalimentación', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Seguridad y Calidad en el Desarrollo de Software
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Seguridad y Calidad en el Desarrollo de Software' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Principios de Seguridad de la Información', v_asignatura_id),
            ('Desarrollo Seguro de Software (SDL)', v_asignatura_id),
            ('Análisis de Vulnerabilidades y Pruebas de Penetración', v_asignatura_id),
            ('Modelos y Estándares de Calidad de Software (ISO 25000)', v_asignatura_id),
            ('Aseguramiento de la Calidad (QA) y Control de Calidad (QC)', v_asignatura_id),
            ('Auditoría de Seguridad y Calidad', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Taller Aplicado de Software
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Taller Aplicado de Software' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Definición y Alcance de un Proyecto de Software', v_asignatura_id),
            ('Diseño y Arquitectura de la Solución', v_asignatura_id),
            ('Desarrollo Incremental del Proyecto', v_asignatura_id),
            ('Integración de Componentes y Pruebas', v_asignatura_id),
            ('Presentación y Demostración del Proyecto', v_asignatura_id),
            ('Documentación Técnica y de Usuario', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Taller de Base de Datos
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Taller de Base de Datos' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Diseño e Implementación de un Modelo de Datos', v_asignatura_id),
            ('Consultas SQL Avanzadas y Reportes', v_asignatura_id),
            ('Programación en PL/SQL o T-SQL', v_asignatura_id),
            ('Manejo de Transacciones y Bloqueos', v_asignatura_id),
            ('Técnicas de Optimización de Rendimiento', v_asignatura_id),
            ('Proyecto Práctico de Base de Datos', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

    -- Taller de Tecnologías de Vanguardia
    SELECT id INTO v_asignatura_id FROM asignaturas WHERE nombre = 'Taller de Tecnologías de Vanguardia' LIMIT 1;
    IF v_asignatura_id IS NOT NULL THEN
        INSERT INTO temas (nombre, id_asignatura) VALUES
            ('Exploración de Tendencias Tecnológicas Emergentes', v_asignatura_id),
            ('Inteligencia Artificial y Machine Learning', v_asignatura_id),
            ('Blockchain y Criptomonedas', v_asignatura_id),
            ('Internet de las Cosas (IoT)', v_asignatura_id),
            ('Realidad Virtual y Aumentada', v_asignatura_id),
            ('Computación Cuántica', v_asignatura_id)
        ON CONFLICT (nombre, id_asignatura) DO NOTHING;
    END IF;

END $$;
