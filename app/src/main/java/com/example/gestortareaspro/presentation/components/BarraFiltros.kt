package com.example.gestortareaspro.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestortareaspro.presentation.viewmodel.FiltroTarea

@Composable
fun BarraFiltros(
    filtroActual: FiltroTarea,
    onFiltroSeleccionado: (FiltroTarea) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FiltroTarea.entries.forEach { filtro ->
            val seleccionado = filtro == filtroActual
            FilterChip(
                selected = seleccionado,
                onClick = { onFiltroSeleccionado(filtro) },
                label = {
                    Text(
                        text = filtro.etiqueta,
                        fontSize = 13.sp,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = null
            )
        }
    }
}
