package com.example.gestortareaspro.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestortareaspro.domain.model.CategoriaHabito
import com.example.gestortareaspro.domain.model.Habito

/**
 * Tarjeta visual de un hábito con:
 * - Animaciones de color y escala al completar
 * - Indicador lateral con color de categoría
 * - Badge de racha con datos reales de Room
 * - Badge de categoría
 */
@Composable
fun TarjetaHabito(
    habito: Habito,
    onToggle: () -> Unit,
    onVerDetalle: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animación de color al completar
    val colorFondo by animateColorAsState(
        targetValue = if (habito.completadoHoy)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(500),
        label = "colorFondoHabito"
    )

    // Color lateral basado en categoría (o estado de completado)
    val colorBorde by animateColorAsState(
        targetValue = if (habito.completadoHoy)
            MaterialTheme.colorScheme.primary
        else
            habito.categoria.color.copy(alpha = 0.7f),
        animationSpec = tween(500),
        label = "colorBordeHabito"
    )

    // Animación de escala: bounce sutil al completar
    val escala by animateFloatAsState(
        targetValue = if (habito.completadoHoy) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "escalaHabito"
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .scale(escala),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (habito.completadoHoy) 0.dp else 3.dp
        ),
        colors = CardDefaults.elevatedCardColors(containerColor = colorFondo)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador lateral con color de categoría
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(5.dp)
                    .background(colorBorde)
            )

            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono emoji + toggle de completar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = habito.icono, fontSize = 28.sp)
                    if (habito.completadoHoy) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Completado",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                // Información del hábito
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habito.nombre,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = if (habito.completadoHoy)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (habito.descripcion.isNotBlank()) {
                        Text(
                            text = habito.descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    // Fila de badges: categoría + racha
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Badge de categoría
                        if (habito.categoria != CategoriaHabito.OTRO) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(habito.categoria.color.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${habito.categoria.emoji} ${habito.categoria.etiqueta}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = habito.categoria.color
                                )
                            }
                        }

                        // Indicador de racha (datos reales de Room)
                        if (habito.rachaActual > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LocalFireDepartment,
                                    contentDescription = "Racha",
                                    tint = Color(0xFFFF6B35),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${habito.rachaActual} día${if (habito.rachaActual != 1) "s" else ""}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF6B35)
                                )
                            }
                        }
                    }
                }

                // Botones de acción
                Row {
                    FilledTonalIconButton(
                        onClick = onVerDetalle,
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Rounded.Info, "Detalle", modifier = Modifier.size(20.dp))
                    }

                    Spacer(Modifier.width(8.dp))

                    FilledTonalIconButton(
                        onClick = onEliminar,
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Rounded.DeleteOutline, "Eliminar", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
