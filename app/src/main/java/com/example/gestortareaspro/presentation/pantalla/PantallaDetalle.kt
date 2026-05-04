package com.example.gestortareaspro.presentation.pantalla

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestortareaspro.domain.model.Habito
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Pantalla de detalle con calendario funcional real,
 * rachas dinámicas y estadísticas conectadas a Room.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    habito: Habito,
    totalDiasCompletados: Int,
    mesSeleccionado: YearMonth,
    fechasCompletadasMes: Set<LocalDate>,
    onVolverAtras: () -> Unit,
    onAlternarCompletado: () -> Unit,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit
) {
    val colorBoton by animateColorAsState(
        targetValue = if (habito.completadoHoy)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.primary,
        animationSpec = tween(500),
        label = "colorBoton"
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del hábito",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolverAtras) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                        Surface(
                shape = CircleShape,
                color = if (habito.completadoHoy)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = habito.icono, fontSize = 48.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

                        Text(
                text = habito.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            if (habito.descripcion.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = habito.descripcion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

                        if (habito.categoria != com.example.gestortareaspro.domain.model.CategoriaHabito.OTRO) {
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(habito.categoria.color.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${habito.categoria.emoji} ${habito.categoria.etiqueta}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = habito.categoria.color
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

                        Button(
                onClick = onAlternarCompletado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorBoton,
                    contentColor = if (habito.completadoHoy)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (habito.completadoHoy) 0.dp else 4.dp
                )
            ) {
                Icon(
                    imageVector = if (habito.completadoHoy)
                        Icons.Rounded.CheckCircle
                    else
                        Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = if (habito.completadoHoy) "✓ Completado hoy"
                    else "Marcar como completado",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(32.dp))

                        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaEstadistica(
                    icono = Icons.Rounded.LocalFireDepartment,
                    colorIcono = Color(0xFFFF6B35),
                    titulo = "Racha actual",
                    valor = "${habito.rachaActual} día${if (habito.rachaActual != 1) "s" else ""}",
                    modifier = Modifier.weight(1f)
                )
                TarjetaEstadistica(
                    icono = Icons.Rounded.EmojiEvents,
                    colorIcono = Color(0xFFFFB300),
                    titulo = "Mejor racha",
                    valor = "${habito.mejorRacha} día${if (habito.mejorRacha != 1) "s" else ""}",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaEstadistica(
                    icono = Icons.Rounded.CalendarMonth,
                    colorIcono = MaterialTheme.colorScheme.primary,
                    titulo = "Total completados",
                    valor = "$totalDiasCompletados día${if (totalDiasCompletados != 1) "s" else ""}",
                    modifier = Modifier.weight(1f)
                )
                TarjetaEstadistica(
                    icono = Icons.Rounded.Star,
                    colorIcono = Color(0xFF06B6D4),
                    titulo = "Estado hoy",
                    valor = if (habito.completadoHoy) "Completado ✓" else "Pendiente",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(32.dp))

                        // CALENDARIO FUNCIONAL — Datos reales de Room
                        CalendarioHabito(
                mesSeleccionado = mesSeleccionado,
                fechasCompletadas = fechasCompletadasMes,
                onMesAnterior = onMesAnterior,
                onMesSiguiente = onMesSiguiente
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

/**
 * Calendario funcional del mes con navegación.
 * Cada día se colorea según el historial real de Room.
 * Muestra encabezados de día de la semana (L, M, X, J, V, S, D).
 */
@Composable
private fun CalendarioHabito(
    mesSeleccionado: YearMonth,
    fechasCompletadas: Set<LocalDate>,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit
) {
    val hoy = LocalDate.now()
    val esEsteMes = mesSeleccionado == YearMonth.from(hoy)
    val esFuturo = mesSeleccionado.isAfter(YearMonth.from(hoy))

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Encabezado del mes con navegación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMesAnterior) {
                    Icon(
                        Icons.Rounded.ChevronLeft,
                        contentDescription = "Mes anterior",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "${mesSeleccionado.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                        .replaceFirstChar { it.uppercase() }} ${mesSeleccionado.year}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onMesSiguiente,
                    enabled = !esFuturo
                ) {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = "Mes siguiente",
                        tint = if (esFuturo)
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Encabezados de días de la semana
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val diasSemana = listOf("L", "M", "X", "J", "V", "S", "D")
                diasSemana.forEach { dia ->
                    Text(
                        text = dia,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Construir grid del calendario
            val primerDiaMes = mesSeleccionado.atDay(1)
            val totalDias = mesSeleccionado.lengthOfMonth()

            // Calcular offset: en qué columna empieza el día 1
            // Lunes=0, Martes=1, ..., Domingo=6
            val offsetInicio = (primerDiaMes.dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7

            // Crear lista de celdas: nulls para offset + días reales
            val celdas = mutableListOf<LocalDate?>()
            repeat(offsetInicio) { celdas.add(null) }
            for (dia in 1..totalDias) {
                celdas.add(mesSeleccionado.atDay(dia))
            }

            // Grid de 7 columnas (Lunes a Domingo)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(((celdas.size / 7 + if (celdas.size % 7 > 0) 1 else 0) * 44).dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                userScrollEnabled = false
            ) {
                items(celdas) { fecha ->
                    if (fecha == null) {
                        // Celda vacía (offset)
                        Box(modifier = Modifier.aspectRatio(1f))
                    } else {
                        val completado = fechasCompletadas.contains(fecha)
                        val esHoy = fecha == hoy
                        val esFuturoFecha = fecha.isAfter(hoy)

                        CeldaDiaCalendario(
                            dia = fecha.dayOfMonth,
                            completado = completado,
                            esHoy = esHoy,
                            esFuturo = esFuturoFecha
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Leyenda
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Completado
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Completado",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.width(20.dp))

                // Pendiente
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Pendiente",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.width(20.dp))

                // Hoy
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .then(
                            Modifier.background(
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                CircleShape
                            )
                        )
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Hoy",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Celda individual del calendario.
 * Coloreada según estado real del historial.
 */
@Composable
private fun CeldaDiaCalendario(
    dia: Int,
    completado: Boolean,
    esHoy: Boolean,
    esFuturo: Boolean
) {
    val colorFondo = when {
        completado -> MaterialTheme.colorScheme.primary
        esHoy -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
        esFuturo -> Color.Transparent
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val colorTexto = when {
        completado -> MaterialTheme.colorScheme.onPrimary
        esHoy -> MaterialTheme.colorScheme.tertiary
        esFuturo -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colorFondo)
            .then(
                if (esHoy && !completado) Modifier.background(
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                    RoundedCornerShape(8.dp)
                ) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$dia",
            fontSize = 13.sp,
            fontWeight = if (completado || esHoy) FontWeight.Bold else FontWeight.Normal,
            color = colorTexto
        )
    }
}

/**
 * Tarjeta de estadística con icono, valor y título.
 */
@Composable
private fun TarjetaEstadistica(
    icono: ImageVector,
    colorIcono: Color,
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = colorIcono,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = valor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = titulo,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
