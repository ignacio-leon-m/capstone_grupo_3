# Etapa 1: Construcción
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle

# Copiar código fuente
COPY src src
COPY hangman-game hangman-game

# Construir la aplicación
RUN ./gradlew build --no-daemon -x test

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiar el JAR construido desde la etapa anterior
COPY --from=build /app/build/libs/*.jar app.jar

# Crear directorio para uploads
RUN mkdir -p /app/uploads

# Exponer el puerto (ajusta según tu aplicación)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
