# ===================================
# Multi-stage Dockerfile for Render
# ===================================

# Etapa 1: Construcción
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle

# Copiar código fuente
COPY src src
COPY hangman-game hangman-game

# Construir la aplicación (sin tests para optimizar)
RUN ./gradlew build --no-daemon -x test

# Etapa 2: Ejecución (imagen más ligera)
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copiar el JAR construido desde la etapa anterior
COPY --from=build /app/build/libs/CapstoneBackend-0.0.1-SNAPSHOT.jar app.jar

# Crear directorio para uploads
RUN mkdir -p /app/uploads

# Exponer puerto (Render usa variable $PORT)
EXPOSE 8080

# Healthcheck para Render
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Comando para ejecutar con configuración optimizada para Render
ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dserver.port=${PORT:-8080}", \
  "-jar", "app.jar"]
