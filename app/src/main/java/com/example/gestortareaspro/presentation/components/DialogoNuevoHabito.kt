package com.example.gestortareaspro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestortareaspro.domain.model.CategoriaHabito

/**
 * Diálogo modal para crear un nuevo hábito.
 * Permite ingresar nombre, descripción, seleccionar icono y categoría.
 *
 * @param onConfirmar Callback con nombre, descripción, icono y categoría
 * @param onCancelar Callback al cancelar la creación
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DialogoNuevoHabito(
    onConfirmar: (nombre: String, descripcion: String, icono: String, categoria: CategoriaHabito) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var icono by remember { mutableStateOf("🎯") }
    var categoriaSeleccionada by remember { mutableStateOf(CategoriaHabito.OTRO) }

    val coloresTextField = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary
    )

    AlertDialog(
        onDismissRequest = onCancelar,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "Nuevo hábito",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                // Campo de nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del hábito") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresTextField
                )

                Spacer(Modifier.height(12.dp))

                // Campo de descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresTextField
                )

                Spacer(Modifier.height(16.dp))

                // Selector de icono
                Text(
                    text = "Icono",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                SelectorIcono(
                    iconoSeleccionado = icono,
                    onIconoSeleccionado = { icono = it }
                )

                Spacer(Modifier.height(16.dp))

                // Selector de categoría
                Text(
                    text = "Categoría",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoriaHabito.entries.forEach { categoria ->
                        val seleccionada = categoria == categoriaSeleccionada
                        FilterChip(
                            selected = seleccionada,
                            onClick = { categoriaSeleccionada = categoria },
                            label = {
                                Text(
                                    text = "${categoria.emoji} ${categoria.etiqueta}",
                                    fontSize = 12.sp,
                                    fontWeight = if (seleccionada) FontWeight.Bold
                                    else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(categoria.colorHex).copy(alpha = 0.2f),
                                selectedLabelColor = Color(categoria.colorHex),
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    .copy(alpha = 0.4f),
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = if (seleccionada)
                                FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = true,
                                    borderColor = Color(categoria.colorHex).copy(alpha = 0.5f)
                                )
                            else null
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val nombreLimpio = nombre.trim()
                    if (nombreLimpio.isNotBlank()) {
                        onConfirmar(nombreLimpio, descripcion.trim(), icono, categoriaSeleccionada)
                    }
                }
            ) {
                Text(
                    "Crear",
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
