package com.example.gestortareaspro.domain.model

/**
 * Modelo de dominio puro para un hábito.
 * Contiene toda la información del hábito incluyendo
 * categoría, rachas calculadas y estado diario.
 */
data class Habito(
    val id: Int = 0,
    val nombre: String,
    val descripcion: String = "",
    val icono: String = "🎯",
    val categoria: CategoriaHabito = CategoriaHabito.OTRO,
    val rachaActual: Int = 0,
    val mejorRacha: Int = 0,
    val completadoHoy: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis()
)
