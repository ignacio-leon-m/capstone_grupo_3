package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID

/**
 * Load Type Catalog - Types of bulk loads supported by the system.
 * Referenced by project report section 1.4 "Tablas de Carga y Auditoría".
 */
@Entity
@Table(name = "tipos_carga", schema = "public")
class LoadType(
    @Column(name = "nombre_tipo", nullable = false, unique = true, length = 100)
    var typeName: String,

    @Column(name = "descripcion", columnDefinition = "text")
    var description: String? = null
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}

/**
 * Load Status Catalog - Status of bulk load operations.
 * Referenced by project report section 1.4 "Tablas de Carga y Auditoría".
 */
@Entity
@Table(name = "estados_carga", schema = "public")
class LoadStatus(
    @Column(name = "nombre_estado", nullable = false, unique = true, length = 100)
    var statusName: String
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}

/**
 * Audit record for bulk load operations (users, content, etc.).
 * Provides traceability as specified in project report.
 * 
 * Tracks:
 * - Who performed the load
 * - When it happened
 * - What file was used
 * - Success/failure status
 * - Error details if failed
 */
@Entity
@Table(name = "cargas", schema = "public")
class Load(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_carga", nullable = false)
    var uploadedBy: User,

    @Column(name = "fecha_hora_carga", nullable = false)
    var loadDateTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    var fileName: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estado", nullable = false)
    var status: LoadStatus,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_carga", nullable = false)
    var loadType: LoadType,

    @Column(name = "detalle_error", columnDefinition = "text")
    var errorDetail: String? = null,

    @Column(name = "registros_procesados")
    var recordsProcessed: Int? = null,

    @Column(name = "registros_exitosos")
    var recordsSuccessful: Int? = null,

    @Column(name = "registros_fallidos")
    var recordsFailed: Int? = null
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}
