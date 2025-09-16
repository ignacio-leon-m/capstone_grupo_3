# Capstone Backend

## Descripción

Este es el backend para el proyecto Capstone. Proporciona una API REST para gestionar los datos de la aplicación, incluyendo la autenticación de usuarios, la persistencia de datos y la carga de archivos. El proyecto está construido con Kotlin y Spring Boot, siguiendo las mejores prácticas de desarrollo de software.

## Tecnologías Utilizadas

*   **Lenguaje**: Kotlin
*   **Framework**: Spring Boot 3.5.5
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
*   `dto`: Contiene los Data Transfer Objects, utilizados para transferir datos entre las capas de la aplicación.
*   `config`: Contiene las clases de configuración de Spring.
*   `exception`: Contiene las clases de manejo de excepciones personalizadas.
*   `util`: Contiene clases de utilidad.

## Funcionalidades Implementadas

*   **API REST**: Se ha implementado una API REST completa para realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre los recursos de la aplicación.
*   **Autenticación y Autorización**: Se utiliza Spring Security para proteger los endpoints de la API, asegurando que solo los usuarios autenticados y autorizados puedan acceder a los recursos.
*   **Persistencia de Datos con PostgreSQL**: La información se almacena en una base de datos PostgreSQL, y se utiliza Spring Data JPA para facilitar el acceso y la manipulación de los datos.
*   **Migraciones de Base de Datos con Flyway**: Se utiliza Flyway para versionar y gestionar las migraciones de la base de datos de forma automática.
*   **Carga de Archivos Excel**: Se ha implementado una funcionalidad para cargar y procesar archivos de formato Excel (.xlsx) utilizando la librería Apache POI.

## Cómo Empezar

Para ejecutar el proyecto en un entorno local, sigue estos pasos:

### Prerrequisitos

*   JDK 21 o superior.
*   Gradle 8.x o superior.
*   Una instancia de PostgreSQL en ejecución.

### Pasos

1.  **Clona el repositorio:**
    ```bash
    git clone <URL_DEL_REPOSITORIO>
    cd capstone-backend-code/CapstoneBackend
    ```

2.  **Configura la base de datos:**
    Crea un archivo `application.properties` en `src/main/resources` y configura las propiedades de conexión a tu base de datos PostgreSQL:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/tu_base_de_datos
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    spring.jpa.hibernate.ddl-auto=validate
    ```

3.  **Ejecuta la aplicación:**
    Puedes ejecutar la aplicación utilizando el wrapper de Gradle:
    ```bash
    ./gradlew bootRun
    ```

La aplicación se iniciará en el puerto 8080 por defecto.
