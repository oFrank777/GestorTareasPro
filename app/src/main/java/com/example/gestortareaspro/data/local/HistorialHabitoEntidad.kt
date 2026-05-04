package com.example.gestortareaspro.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.gestortareaspro.domain.model.HistorialHabito

/**
 * Entidad persistente que registra si un hábito fue completado
 * en una fecha específica. Tiene clave foránea hacia [HabitoEntidad]
 * y un índice único compuesto (habitoId + fecha) para evitar duplicados.
 */
@Entity(
    tableName = "historial_habitos",
    foreignKeys = [
        ForeignKey(
            entity = HabitoEntidad::class,
            parentColumns = ["id"],
            childColumns = ["habito_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habito_id", "fecha"], unique = true),
        Index(value = ["habito_id"])
    ]
)
data class HistorialHabitoEntidad(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "habito_id")
    val habitoId: Int,

    @ColumnInfo(name = "fecha")
    val fecha: String,

    @ColumnInfo(name = "completado")
    val completado: Boolean
) {
        fun aModelo(): HistorialHabito = HistorialHabito(
        id = id,
        habitoId = habitoId,
        fecha = fecha,
        completado = completado
    )

    companion object {
                fun desdeModelo(historial: HistorialHabito): HistorialHabitoEntidad =
            HistorialHabitoEntidad(
                id = historial.id,
                habitoId = historial.habitoId,
                fecha = historial.fecha,
                completado = historial.completado
            )
    }
}
