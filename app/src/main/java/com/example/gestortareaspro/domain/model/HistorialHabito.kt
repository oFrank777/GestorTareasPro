package com.example.gestortareaspro.domain.model

/**
 * Registro diario del cumplimiento de un hábito.
 * Cada entrada representa si un hábito fue completado
 * en una fecha específica.
 */
data class HistorialHabito(
    val id: Int = 0,
    val habitoId: Int,
    val fecha: String,
    val completado: Boolean
)
