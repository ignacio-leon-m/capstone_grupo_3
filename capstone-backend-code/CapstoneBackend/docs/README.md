# Capstone Backend

## Descripción

Este es el backend para el proyecto Capstone. Proporciona una API REST para gestionar los datos de la aplicación, incluyendo la autenticación de usuarios, la persistencia de datos y la carga masiva de usuarios a través de archivos Excel. El proyecto está construido con Kotlin y Spring Boot, siguiendo las mejores prácticas de desarrollo de software y una arquitectura limpia.

## Tecnologías Utilizadas

*   **Lenguaje**: Kotlin
*   **Framework**: Spring Boot 3.3.0
*   **Base de Datos**: PostgreSQL
*   **Migraciones de Base de Datos**: Flyway
*   **Seguridad**: Spring Security
*   **Persistencia de Datos**: Spring Data JPA
*   **Manejo de Archivos**: Apache POI
*   **Build Tool**: Gradle

## Estructura del Proyecto

El proyecto sigue una arquitectura en capas, separando las responsabilidades en diferentes paquetes:

*   `controller`: Contiene los controladores REST que exponen los endpoints de la API.
*   `service`: Contiene la lógica de negocio de la aplicación.
*   `repository`: Contiene las interfaces de Spring Data JPA para interactuar con la base de datos.
*   `model`: Contiene las entidades de JPA que representan las tablas de la base de datos.
*   `dto`: Contiene los Data Transfer Objects (DTOs), utilizados para transferir datos de forma segura entre las capas.
*   `config`: Contiene las clases de configuración de Spring, como la configuración de seguridad.
*   `exception`: Contiene las clases para el manejo de excepciones personalizadas.
*   `util`: Contiene clases de utilidad, como `StringUtils` para la manipulación y normalización de texto.

## Funcionalidades Implementadas

*   **API REST**: Endpoints para realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre los recursos de la aplicación.
*   **Autenticación y Autorización**: Se utiliza Spring Security para proteger los endpoints de la API, asegurando que solo los usuarios autenticados y con los roles adecuados puedan acceder a los recursos.
*   **Persistencia de Datos con PostgreSQL**: La información se almacena en una base de datos PostgreSQL. Se utiliza Spring Data JPA para un acceso y manipulación de datos eficiente.
*   **Migraciones de Base de Datos con Flyway**: Se gestiona el versionado del esquema de la base de datos de forma automática y segura.
*   **Carga de Archivos Excel**: Funcionalidad para cargar y procesar archivos `.xlsx` utilizando Apache POI. Esto permite la creación masiva de usuarios a partir de un formato predefinido, incluyendo validaciones y normalización de datos.
*   **Normalización de Datos**: Se aplican funciones de utilidad para limpiar y estandarizar los datos de entrada, como la capitalización correcta de nombres completos.

## Cómo Empezar

Para ejecutar el proyecto en un entorno local, sigue estos pasos:

### Prerrequisitos

*   JDK 21 o superior.
*   Gradle 8.x o superior.
*   Una instancia de PostgreSQL en ejecución.

### Pasos

1.  **Clona el repositorio:**
    ```bash
    git clone https://github.com/ignacio-leon-m/capstone_grupo_3.git
    cd capstone_grupo_3/CapstoneBackend
    ```

2.  **Configura la base de datos:**
    Crea un archivo `application.properties` en `src/main/resources` y configura las propiedades de conexión a tu base de datos PostgreSQL:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/nombre_tu_db
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    spring.jpa.hibernate.ddl-auto=validate
    ```

3.  **Ejecuta la aplicación:**
    Puedes ejecutar la aplicación utilizando el wrapper de Gradle:
    ```bash
    ./gradlew bootRun
    ```

La aplicación se iniciará en el puerto `8080` por defecto.
