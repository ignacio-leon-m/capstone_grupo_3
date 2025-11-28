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
RUN chmod +x ./gradlew
RUN ./gradlew build --no-daemon -x test

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copiar el JAR construido desde la etapa anterior
COPY --from=build /app/build/libs/*.jar app.jar

# Crear directorio para uploads
RUN mkdir -p /app/uploads

# Exponer puerto
EXPOSE 8080

# Instalar curl y tesseract (OCR) + idioma español
RUN apt-get update && apt-get install -y curl tesseract-ocr tesseract-ocr-spa && rm -rf /var/lib/apt/lists/*

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dserver.port=${PORT:8080}", \
  "-Xmx512m", \
  "-Xms256m", \
  "-jar", "app.jar"]
