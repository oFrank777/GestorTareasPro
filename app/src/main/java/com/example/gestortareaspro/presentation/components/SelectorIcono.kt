package com.example.gestortareaspro.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Lista de emojis disponibles para personalizar hábitos. */
val ICONOS_HABITOS = listOf(
    "🎯", "💪", "📚", "🏃", "🧘", "💧", "🍎", "😴",
    "✍️", "🎨", "🎵", "🧹", "💊", "🌱", "🐕", "📝",
    "💻", "🏋️", "🚴", "🧠", "☀️", "🌙", "❤️", "⭐"
)

/**
 * Selector visual de emojis para personalizar un hábito.
 * Muestra una grilla de emojis con resaltado en el seleccionado.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorIcono(
    iconoSeleccionado: String,
    onIconoSeleccionado: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ICONOS_HABITOS.forEach { icono ->
            val seleccionado = icono == iconoSeleccionado
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (seleccionado)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(44.dp)
                    .clickable { onIconoSeleccionado(icono) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = icono,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}
