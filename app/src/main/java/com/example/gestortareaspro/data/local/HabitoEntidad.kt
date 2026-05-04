package com.example.gestortareaspro.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gestortareaspro.domain.model.CategoriaHabito
import com.example.gestortareaspro.domain.model.Habito

/**
 * Entidad persistente de Room para hábitos.
 * Contiene funciones de mapeo bidireccional hacia/desde
 * el modelo de dominio [Habito].
 */
@Entity(tableName = "habitos")
data class HabitoEntidad(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "descripcion")
    val descripcion: String = "",

    @ColumnInfo(name = "icono")
    val icono: String = "🎯",

    @ColumnInfo(name = "categoria")
    val categoria: String = CategoriaHabito.OTRO.name,

    @ColumnInfo(name = "racha_actual")
    val rachaActual: Int = 0,

    @ColumnInfo(name = "mejor_racha")
    val mejorRacha: Int = 0,

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: Long = System.currentTimeMillis()
) {
        fun aModelo(completadoHoy: Boolean = false): Habito = Habito(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        icono = icono,
        categoria = CategoriaHabito.desdeNombre(categoria),
        rachaActual = rachaActual,
        mejorRacha = mejorRacha,
        completadoHoy = completadoHoy,
        fechaCreacion = fechaCreacion
    )

    companion object {
                fun desdeModelo(habito: Habito): HabitoEntidad = HabitoEntidad(
            id = habito.id,
            nombre = habito.nombre,
            descripcion = habito.descripcion,
            icono = habito.icono,
            categoria = habito.categoria.name,
            rachaActual = habito.rachaActual,
            mejorRacha = habito.mejorRacha,
            fechaCreacion = habito.fechaCreacion
        )
    }
}
