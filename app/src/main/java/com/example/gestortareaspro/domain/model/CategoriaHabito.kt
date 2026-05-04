package com.example.gestortareaspro.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Categorías predefinidas para clasificar hábitos.
 * Cada categoría tiene un icono emoji, un color representativo
 * y una etiqueta en español para la UI.
 */
enum class CategoriaHabito(
    val etiqueta: String,
    val emoji: String,
    val colorHex: Long
) {
    SALUD("Salud", "💪", 0xFF4CAF50),
    MENTE("Mente", "🧠", 0xFF9C27B0),
    DEPORTE("Deporte", "🏃", 0xFFFF5722),
    ESTUDIO("Estudio", "📚", 0xFF2196F3),
    ALIMENTACION("Alimentación", "🥗", 0xFF8BC34A),
    SUEÑO("Sueño", "😴", 0xFF3F51B5),
    PRODUCTIVIDAD("Productividad", "⚡", 0xFFFF9800),
    SOCIAL("Social", "🤝", 0xFFE91E63),
    OTRO("Otro", "🎯", 0xFF607D8B);

    /** Retorna el Color de Compose asociado a esta categoría. */
    val color: Color get() = Color(colorHex)

    companion object {
        /** Busca una categoría por nombre; retorna OTRO si no existe. */
        fun desdeNombre(nombre: String): CategoriaHabito {
            return entries.find { it.name == nombre } ?: OTRO
        }
    }
}
