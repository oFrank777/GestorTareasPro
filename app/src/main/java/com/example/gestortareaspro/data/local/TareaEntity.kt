// ============================================================
// TareaEntity.kt — Entidad Room (Data Layer)
// Mapeada directamente a la tabla SQLite
// ============================================================
package com.example.gestortareaspro.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gestortareaspro.domain.model.Tarea

/**
 * Entidad persistente de Room. Contiene funciones
 * de mapeo bidireccional hacia/desde el modelo de dominio.
 */
@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "titulo")
    val titulo: String,

    @ColumnInfo(name = "completada")
    val completada: Boolean = false
) {
    /** Convierte la entidad Room al modelo de dominio. */
    fun aModelo(): Tarea = Tarea(
        id = id,
        titulo = titulo,
        completada = completada
    )

    companion object {
        /** Convierte un modelo de dominio a entidad Room. */
        fun desdeModelo(tarea: Tarea): TareaEntity = TareaEntity(
            id = tarea.id,
            titulo = tarea.titulo,
            completada = tarea.completada
        )
    }
}
