# Configuración de MongoDB para Docker

## Resumen

Este proyecto utiliza **dos bases de datos**:
- **PostgreSQL (Neon)**: Base de datos principal para datos estructurados (usuarios, juegos, métricas, conceptos)
- **MongoDB**: Base de datos NoSQL para documentos y logs (PDFs procesados, logs de consultas AI, análisis de documentos)

## Configuración de MongoDB en Docker Compose

### Servicio MongoDB

```yaml
mongodb:
  image: mongo:7.0
  container_name: capstone-mongodb
  environment:
    MONGO_INITDB_ROOT_USERNAME: admin
    MONGO_INITDB_ROOT_PASSWORD: adminpass123
    MONGO_INITDB_DATABASE: capstone_docs
  ports:
    - "27017:27017"
  volumes:
    - mongodb_data:/data/db
    - mongodb_config:/data/configdb
  healthcheck:
    test: ["CMD", "mongosh", "--eval", "db.adminCommand('ping')"]
    interval: 10s
    timeout: 5s
    retries: 5
```

### Variables de Entorno

#### Desarrollo (application-dev.properties)

```properties
# MongoDB - Habilitado para desarrollo local (Docker)
spring.data.mongodb.uri=mongodb://admin:adminpass123@localhost:27017/capstone_docs?authSource=admin
spring.data.mongodb.database=capstone_docs
mongodb.enabled=true
```

#### Producción (application-prod.properties)

```properties
# MongoDB - Configuración de producción
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI}
spring.data.mongodb.database=${SPRING_DATA_MONGODB_DATABASE:capstone_docs}
mongodb.enabled=${MONGODB_ENABLED:true}
```

## Colecciones MongoDB

### 1. stored_documents
**Propósito**: Almacenar metadatos de archivos PDF subidos

```kotlin
@Document(collection = "stored_documents")
data class StoredDocument(
    @Id val id: String = UUID.randomUUID().toString(),
    val userId: UUID,
    val fileName: String,
    val filePath: String,
    val mimeType: String,
    val sizeBytes: Long,
    val sha1: String,
    val createdAt: Instant = Instant.now()
)
```

**Uso**: FileUploadService

### 2. ai_query_logs
**Propósito**: Registrar todas las consultas a la IA (Gemini)

```kotlin
@Document(collection = "ai_query_logs")
data class AiQueryLog(
    @Id val id: String = UUID.randomUUID().toString(),
    val userId: UUID,
    val textSha1: String,
    val prompt: String,
    val response: String,
    val createdAt: Instant = Instant.now()
)
```

**Uso**: PersistenceAiService

### 3. document_analysis
**Propósito**: Almacenar análisis de documentos PDF realizados por IA

```kotlin
@Document
data class DocumentAnalysis(
    @Id val id: String? = null,
    @Field("original_filename") val originalFilename: String,
    @Field("file_content_base64") val fileContentBase64: String,
    val analysisResult: String,
    val analysisDate: LocalDateTime = LocalDateTime.now(),
    @Field("user_id") val userId: String? = null
)
```

**Uso**: AiPdfController

## Comandos de Inicialización

### Iniciar MongoDB con Docker Compose

```bash
# Iniciar MongoDB y PostgreSQL
docker-compose up -d mongodb postgres

# Ver logs de MongoDB
docker-compose logs -f mongodb

# Verificar que MongoDB está funcionando
docker exec -it capstone-mongodb mongosh --username admin --password adminpass123
```

### Comandos de MongoDB Shell

```bash
# Conectarse a MongoDB
docker exec -it capstone-mongodb mongosh -u admin -p adminpass123 --authenticationDatabase admin

# Ver bases de datos
show dbs

# Usar la base de datos del proyecto
use capstone_docs

# Ver colecciones
show collections

# Ver documentos en stored_documents
db.stored_documents.find().pretty()

# Ver documentos en ai_query_logs
db.ai_query_logs.find().pretty()

# Contar documentos
db.stored_documents.countDocuments()
db.ai_query_logs.countDocuments()
```

## Repositorios Spring Data MongoDB

### StoredDocumentRepository

```kotlin
interface StoredDocumentRepository : MongoRepository<StoredDocument, String> {
    fun findBySha1(sha1: String): StoredDocument?
}
```

### AiQueryLogRepository

```kotlin
interface AiQueryLogRepository : MongoRepository<AiQueryLog, String>
```

### DocumentAnalysisRepository

```kotlin
interface DocumentAnalysisRepository : MongoRepository<DocumentAnalysis, String> {
    fun findByUserId(userId: String): List<DocumentAnalysis>
}
```

## Flujo de Trabajo

### 1. Subida de Archivo PDF

```mermaid
graph LR
    A[Usuario sube PDF] --> B[FileUploadController]
    B --> C[FileUploadService]
    C --> D[Calcular SHA1]
    D --> E[Verificar duplicado en MongoDB]
    E -->|Existe| F[Retornar existente]
    E -->|No existe| G[Guardar en filesystem]
    G --> H[Guardar metadata en MongoDB]
    H --> I[Retornar StoredDocument]
```

### 2. Consulta a IA con Persistencia

```mermaid
graph LR
    A[Consulta IA] --> B[PersistenceAiService]
    B --> C[CachingAiService]
    C --> D[GeminiService]
    D --> E[Respuesta IA]
    E --> F[Guardar en AiQueryLog MongoDB]
    F --> G[Retornar respuesta]
```

### 3. Análisis de PDF con IA

```mermaid
graph LR
    A[Usuario solicita análisis] --> B[AiPdfController]
    B --> C[FileUploadService: Guardar PDF]
    C --> D[PdfTextExtractor: Extraer texto]
    D --> E[GeminiService: Analizar texto]
    E --> F[Guardar en DocumentAnalysis MongoDB]
    F --> G[Retornar resultado]
```

## Índices Recomendados

### stored_documents

```javascript
// Índice único por SHA1 (evita duplicados)
db.stored_documents.createIndex({ "sha1": 1 }, { unique: true })

// Índice por userId para búsquedas rápidas
db.stored_documents.createIndex({ "userId": 1 })

// Índice por fecha de creación
db.stored_documents.createIndex({ "createdAt": -1 })
```

### ai_query_logs

```javascript
// Índice compuesto por userId y fecha
db.ai_query_logs.createIndex({ "userId": 1, "createdAt": -1 })

// Índice por textSha1 para caché
db.ai_query_logs.createIndex({ "textSha1": 1 })
```

### document_analysis

```javascript
// Índice por userId
db.document_analysis.createIndex({ "user_id": 1 })

// Índice por fecha
db.document_analysis.createIndex({ "analysisDate": -1 })
```

## Migración de Datos (Si es necesario)

Si ya tienes datos en otro sistema y necesitas migrarlos a MongoDB:

```bash
# Exportar datos (ejemplo)
mongoexport --uri="mongodb://admin:adminpass123@localhost:27017/capstone_docs?authSource=admin" --collection=stored_documents --out=stored_documents.json

# Importar datos
mongoimport --uri="mongodb://admin:adminpass123@localhost:27017/capstone_docs?authSource=admin" --collection=stored_documents --file=stored_documents.json
```

## Backup y Restore

### Backup de MongoDB

```bash
# Backup completo
docker exec capstone-mongodb mongodump --username admin --password adminpass123 --authenticationDatabase admin --out /backup

# Copiar backup del contenedor al host
docker cp capstone-mongodb:/backup ./mongodb-backup-$(date +%Y%m%d)
```

### Restore de MongoDB

```bash
# Copiar backup al contenedor
docker cp ./mongodb-backup-20250122 capstone-mongodb:/restore

# Restaurar
docker exec capstone-mongodb mongorestore --username admin --password adminpass123 --authenticationDatabase admin /restore
```

## Monitoreo

### Ver estadísticas de MongoDB

```javascript
// En mongosh
use capstone_docs

// Estadísticas de la base de datos
db.stats()

// Estadísticas de colección
db.stored_documents.stats()
db.ai_query_logs.stats()

// Ver tamaño de datos
db.stored_documents.dataSize()
db.ai_query_logs.dataSize()
```

### Verificar conexiones activas

```javascript
db.currentOp()
db.serverStatus().connections
```

## Troubleshooting

### Error: "mongoTemplate' that could not be found"

**Solución**: Verifica que `mongodb.enabled=true` en tu perfil activo y que MongoDB esté corriendo:

```bash
docker ps | grep mongodb
docker-compose logs mongodb
```

### Error: "Authentication failed"

**Solución**: Verifica las credenciales en la URI:

```properties
# Formato correcto
spring.data.mongodb.uri=mongodb://admin:adminpass123@localhost:27017/capstone_docs?authSource=admin
```

### MongoDB no inicia en Docker

```bash
# Ver logs detallados
docker-compose logs mongodb

# Recrear contenedor
docker-compose down -v
docker-compose up -d mongodb
```

### Limpiar datos de MongoDB (desarrollo)

```bash
# Conectarse a MongoDB
docker exec -it capstone-mongodb mongosh -u admin -p adminpass123 --authenticationDatabase admin

# Limpiar colecciones
use capstone_docs
db.stored_documents.deleteMany({})
db.ai_query_logs.deleteMany({})
db.document_analysis.deleteMany({})
```

## Referencias

- [Spring Data MongoDB Documentation](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- [MongoDB Docker Hub](https://hub.docker.com/_/mongo)
- [MongoDB Manual](https://docs.mongodb.com/manual/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)

## Próximos Pasos

1. **Iniciar servicios**: `docker-compose up -d`
2. **Verificar conexión**: Revisar logs del backend
3. **Probar endpoints**: Subir un PDF y verificar que se guarde en MongoDB
4. **Crear índices**: Ejecutar los comandos de índices recomendados
5. **Configurar backup**: Establecer política de backup regular
