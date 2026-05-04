package com.example.gestortareaspro.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Diálogo modal para editar el título de una tarea.
 * Mantiene un estado local para el texto editado,
 * sin afectar la tarea original hasta confirmar.
 *
 * @param tituloActual Título actual de la tarea
 * @param onConfirmar Callback con el nuevo título
 * @param onCancelar Callback al cancelar la edición
 */
@Composable
fun DialogoEdicion(
    tituloActual: String,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var textoEditado by remember { mutableStateOf(tituloActual) }

    AlertDialog(
        onDismissRequest = onCancelar,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "Editar tarea",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            OutlinedTextField(
                value = textoEditado,
                onValueChange = { textoEditado = it },
                label = { Text("Nuevo título") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val textoLimpio = textoEditado.trim()
                    if (textoLimpio.isNotBlank()) {
                        onConfirmar(textoLimpio)
                    }
                }
            ) {
                Text(
                    "Guardar",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text(
                    "Cancelar",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
