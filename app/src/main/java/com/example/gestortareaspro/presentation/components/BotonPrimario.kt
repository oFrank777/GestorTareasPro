// ============================================================
// BotonPrimario.kt — Botón reutilizable con estilo premium
// ============================================================
package com.example.gestortareaspro.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Botón primario reutilizable con esquinas redondeadas
 * y colores del tema Material 3.
 *
 * @param texto Texto a mostrar en el botón
 * @param habilitado Estado de habilitación del botón
 * @param modifier Modifier externo para personalización
 * @param onClick Callback al pulsar el botón
 */
@Composable
fun BotonPrimario(
    texto: String,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        )
    ) {
        Text(
            text = texto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
    }
}
