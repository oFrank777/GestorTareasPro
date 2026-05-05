package com.example.gestortareaspro.presentation.pantalla

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.example.gestortareaspro.presentation.viewmodel.DatosDiaCalendario
import com.example.gestortareaspro.presentation.viewmodel.EstadoCalendarioHome
import com.example.gestortareaspro.presentation.viewmodel.EstadoDiaCalendario
import com.example.gestortareaspro.presentation.viewmodel.HabitoDiaResumen
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestortareaspro.domain.model.CategoriaHabito
import com.example.gestortareaspro.domain.model.Habito
import com.example.gestortareaspro.domain.model.Tarea
import com.example.gestortareaspro.presentation.components.BarraFiltros
import com.example.gestortareaspro.presentation.components.CampoTexto
import com.example.gestortareaspro.presentation.components.DialogoEdicion
import com.example.gestortareaspro.presentation.components.DialogoNuevoHabito
import com.example.gestortareaspro.presentation.components.EstadoVacio
import com.example.gestortareaspro.presentation.components.TarjetaHabito
import com.example.gestortareaspro.presentation.components.TarjetaTarea
import com.example.gestortareaspro.presentation.viewmodel.EstadoPantallaHabitos
import com.example.gestortareaspro.presentation.viewmodel.EstadoPantallaTareas
import com.example.gestortareaspro.presentation.viewmodel.FiltroHabito
import com.example.gestortareaspro.presentation.viewmodel.FiltroTarea

/**
 * Pantalla principal con navegación por pestañas.
 * Muestra saludo personalizado ("Hola, [nombre]")
 * y combina tareas + hábitos con categorías.
 */
@Composable
fun PantallaInicio(
    estadoTareas: EstadoPantallaTareas,
    estadoHabitos: EstadoPantallaHabitos,
    estadoCalendario: EstadoCalendarioHome,
    nombreUsuario: String,
    temaOscuro: Boolean,
    onAlternarTema: () -> Unit,
    // Callbacks de tareas
    onTextoNuevaTareaCambiado: (String) -> Unit,
    onAgregarTarea: () -> Unit,
    onAlternarCompletada: (Tarea) -> Unit,
    onIniciarEdicion: (Tarea) -> Unit,
    onConfirmarEdicion: (String) -> Unit,
    onCancelarEdicion: () -> Unit,
    onEliminarTarea: (Tarea) -> Unit,
    onFiltroTareaSeleccionado: (FiltroTarea) -> Unit,
    // Callbacks de hábitos
    onTextoNuevoHabitoCambiado: (String) -> Unit,
    onDescripcionNuevoHabitoCambiada: (String) -> Unit,
    onIconoNuevoHabitoSeleccionado: (String) -> Unit,
    onCategoriaNuevoHabitoSeleccionada: (CategoriaHabito) -> Unit,
    onAgregarHabito: () -> Unit,
    onAlternarHabitoCompletado: (Habito) -> Unit,
    onEliminarHabito: (Habito) -> Unit,
    onFiltroHabitoSeleccionado: (FiltroHabito) -> Unit,
    onNavegarDetalleHabito: (Int) -> Unit,
    onMesCalendarioAnterior: () -> Unit,
    onMesCalendarioSiguiente: () -> Unit,
    onFechaCalendarioSeleccionada: (LocalDate) -> Unit,
    onCerrarSesion: () -> Unit
) {
    var pestanaSeleccionada by rememberSaveable { mutableIntStateOf(0) }
    var mostrarDialogoNuevoHabito by remember { mutableStateOf(false) }

    val pestanas = listOf(
        Pair("Tareas", Icons.Rounded.Checklist),
        Pair("Hábitos", Icons.Rounded.FitnessCenter)
    )
    val saludo = remember(nombreUsuario) {
        val hora = java.time.LocalTime.now().hour
        val momento = when {
            hora < 12 -> "Buenos días"
            hora < 18 -> "Buenas tardes"
            else -> "Buenas noches"
        }
        if (nombreUsuario.isNotBlank()) "$momento, $nombreUsuario"
        else "Mi Día"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            // FAB solo visible en la pestaña de hábitos
            if (pestanaSeleccionada == 1) {
                FloatingActionButton(
                    onClick = { mostrarDialogoNuevoHabito = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Rounded.Add, "Nuevo hábito")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
                        Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = saludo,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = if (pestanaSeleccionada == 0) "Gestión de Tareas"
                        else "Seguimiento de Hábitos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalIconButton(
                        onClick = onAlternarTema,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = if (temaOscuro) Icons.Default.LightMode
                            else Icons.Default.DarkMode,
                            contentDescription = "Tema",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    FilledTonalIconButton(
                        onClick = onCerrarSesion,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

                        TabRow(
                selectedTabIndex = pestanaSeleccionada,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { posiciones ->
                    if (pestanaSeleccionada < posiciones.size) {
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(posiciones[pestanaSeleccionada]),
                            height = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                pestanas.forEachIndexed { indice, (titulo, icono) ->
                    Tab(
                        selected = pestanaSeleccionada == indice,
                        onClick = { pestanaSeleccionada = indice },
                        text = {
                            Text(
                                text = titulo,
                                fontWeight = if (pestanaSeleccionada == indice)
                                    FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = icono,
                                contentDescription = titulo,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

                        AnimatedContent(
                targetState = pestanaSeleccionada,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "contenidoPestana",
                modifier = Modifier.weight(1f)
            ) { pestaña ->
                when (pestaña) {
                    0 -> ContenidoTareas(
                        estado = estadoTareas,
                        onTextoNuevaTareaCambiado = onTextoNuevaTareaCambiado,
                        onAgregarTarea = onAgregarTarea,
                        onAlternarCompletada = onAlternarCompletada,
                        onIniciarEdicion = onIniciarEdicion,
                        onEliminarTarea = onEliminarTarea,
                        onFiltroSeleccionado = onFiltroTareaSeleccionado
                    )
                    1 -> ContenidoHabitos(
                        estado = estadoHabitos,
                        estadoCalendario = estadoCalendario,
                        onAlternarCompletado = onAlternarHabitoCompletado,
                        onVerDetalle = onNavegarDetalleHabito,
                        onEliminar = onEliminarHabito,
                        onFiltroSeleccionado = onFiltroHabitoSeleccionado,
                        onMesCalendarioAnterior = onMesCalendarioAnterior,
                        onMesCalendarioSiguiente = onMesCalendarioSiguiente,
                        onFechaCalendarioSeleccionada = onFechaCalendarioSeleccionada
                    )
                }
            }
        }
    }
    estadoTareas.tareaEditando?.let { tarea ->
        DialogoEdicion(
            tituloActual = tarea.titulo,
            onConfirmar = onConfirmarEdicion,
            onCancelar = onCancelarEdicion
        )
    }

    if (mostrarDialogoNuevoHabito) {
        DialogoNuevoHabito(
            onConfirmar = { nombre, descripcion, icono, categoria ->
                onTextoNuevoHabitoCambiado(nombre)
                onDescripcionNuevoHabitoCambiada(descripcion)
                onIconoNuevoHabitoSeleccionado(icono)
                onCategoriaNuevoHabitoSeleccionada(categoria)
                onAgregarHabito()
                mostrarDialogoNuevoHabito = false
            },
            onCancelar = { mostrarDialogoNuevoHabito = false }
        )
    }
}


@Composable
private fun ContenidoTareas(
    estado: EstadoPantallaTareas,
    onTextoNuevaTareaCambiado: (String) -> Unit,
    onAgregarTarea: () -> Unit,
    onAlternarCompletada: (Tarea) -> Unit,
    onIniciarEdicion: (Tarea) -> Unit,
    onEliminarTarea: (Tarea) -> Unit,
    onFiltroSeleccionado: (FiltroTarea) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Barra de progreso de tareas
        if (estado.totalTareas > 0) {
            val progreso by androidx.compose.animation.core.animateFloatAsState(
                targetValue = estado.tareasCompletadas.toFloat() / estado.totalTareas.toFloat(),
                animationSpec = androidx.compose.animation.core.tween(800),
                label = "progresoTareas"
            )

            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.layout.Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(42.dp)
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            progress = { progreso },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 4.dp,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Text(
                            "${(progreso * 100).toInt()}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            "Tu progreso de hoy",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "${estado.tareasCompletadas} de ${estado.totalTareas} completadas",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto + botón agregar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CampoTexto(
                valor = estado.textoNuevaTarea,
                onValorChange = onTextoNuevaTareaCambiado,
                etiqueta = "Escribe una tarea...",
                modifier = Modifier.weight(1f)
            )

            FilledIconButton(
                onClick = onAgregarTarea,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Rounded.Add, "Agregar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BarraFiltros(
            filtroActual = estado.filtroActual,
            onFiltroSeleccionado = onFiltroSeleccionado
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Lista de tareas
        AnimatedContent(
            targetState = estado.tareas,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "listaTareasAnimada",
            modifier = Modifier.weight(1f)
        ) { lista ->
            if (lista.isEmpty()) {
                EstadoVacio(
                    mensaje = "Todo despejado",
                    submensaje = "Disfruta de tu tiempo libre o agrega una tarea."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(lista, key = { it.id }) { tarea ->
                        TarjetaTarea(
                            tarea = tarea,
                            onToggle = { onAlternarCompletada(tarea) },
                            onEditar = { onIniciarEdicion(tarea) },
                            onEliminar = { onEliminarTarea(tarea) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ContenidoHabitos(
    estado: EstadoPantallaHabitos,
    estadoCalendario: EstadoCalendarioHome,
    onAlternarCompletado: (Habito) -> Unit,
    onVerDetalle: (Int) -> Unit,
    onEliminar: (Habito) -> Unit,
    onFiltroSeleccionado: (FiltroHabito) -> Unit,
    onMesCalendarioAnterior: () -> Unit,
    onMesCalendarioSiguiente: () -> Unit,
    onFechaCalendarioSeleccionada: (LocalDate) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        if (estado.totalHabitos > 0) {
            item {
                ResumenHabitos(
                    total = estado.totalHabitos,
                    completados = estado.habitosCompletadosHoy
                )
            }
        }

        item {
            CalendarioHabitos(
                estadoCalendario = estadoCalendario,
                onMesAnterior = onMesCalendarioAnterior,
                onMesSiguiente = onMesCalendarioSiguiente,
                onFechaSeleccionada = onFechaCalendarioSeleccionada
            )
        }

        if (estadoCalendario.fechaSeleccionada != null && estadoCalendario.datosDiaSeleccionado != null) {
            item {
                PanelHabitosDia(
                    habitos = estadoCalendario.habitosDiaSeleccionado,
                    datosDia = estadoCalendario.datosDiaSeleccionado
                )
            }
        }

        item {
            BarraFiltrosHabitos(
                filtroActual = estado.filtroActual,
                onFiltroSeleccionado = onFiltroSeleccionado
            )
        }

        if (estado.habitos.isEmpty()) {
            item {
                EstadoVacio(
                    mensaje = "Sin hábitos aún",
                    submensaje = "Toca el botón + para crear tu primer hábito."
                )
            }
        } else {
            items(estado.habitos, key = { it.id }) { habito ->
                TarjetaHabito(
                    habito = habito,
                    onToggle = { onAlternarCompletado(habito) },
                    onVerDetalle = { onVerDetalle(habito.id) },
                    onEliminar = { onEliminar(habito) }
                )
            }
        }
    }
}

@Composable
fun CalendarioHabitos(
    estadoCalendario: EstadoCalendarioHome,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit,
    onFechaSeleccionada: (LocalDate) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMesAnterior) {
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = "Mes Anterior")
            }
            Text(
                text = estadoCalendario.mesSeleccionado.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.uppercase() } + " " + estadoCalendario.mesSeleccionado.year,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onMesSiguiente) {
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Mes Siguiente")
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEach { dia ->
                Text(
                    text = dia,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        val primerDiaDelMes = estadoCalendario.mesSeleccionado.atDay(1)
        val longitudMes = estadoCalendario.mesSeleccionado.lengthOfMonth()
        val primerDiaSemana = primerDiaDelMes.dayOfWeek.value
        val diasPrevios = primerDiaSemana - 1
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.heightIn(max = 280.dp),
            userScrollEnabled = false
        ) {
            items(diasPrevios) {
                Box(modifier = Modifier.aspectRatio(1f))
            }
            items(longitudMes) { dia ->
                val fecha = primerDiaDelMes.plusDays(dia.toLong())
                val seleccionada = estadoCalendario.fechaSeleccionada == fecha
                val datosDia = estadoCalendario.datosPorDia[fecha]
                
                val estadoColor = when (datosDia?.estado) {
                    EstadoDiaCalendario.COMPLETO -> Color(0xFF4CAF50)
                    EstadoDiaCalendario.PARCIAL -> Color(0xFFFFA000)
                    EstadoDiaCalendario.FALLADO -> Color(0xFFF44336)
                    else -> Color.LightGray
                }
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(if (seleccionada) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable { onFechaSeleccionada(fecha) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (dia + 1).toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
                            color = if (seleccionada) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(estadoColor))
                    }
                }
            }
        }
    }
}

@Composable
fun PanelHabitosDia(habitos: List<HabitoDiaResumen>, datosDia: DatosDiaCalendario) {
    if (habitos.isEmpty() && datosDia.estado == EstadoDiaCalendario.SIN_REGISTRO) return

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hábitos del día", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            if (datosDia.estado != EstadoDiaCalendario.SIN_REGISTRO) {
                Text(
                    text = "${datosDia.porcentaje.toInt()}% completado",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (datosDia.estado) {
                        EstadoDiaCalendario.COMPLETO -> Color(0xFF4CAF50)
                        EstadoDiaCalendario.PARCIAL -> Color(0xFFFFA000)
                        EstadoDiaCalendario.FALLADO -> Color(0xFFF44336)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
        
        if (habitos.isEmpty()) {
            Text(
                text = "No tenías hábitos creados en esta fecha.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        habitos.forEach { habito ->
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(habito.icono, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                        Text(habito.nombre, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (habito.completado) {
                        Text("✅", fontSize = 16.sp)
                    } else {
                        Text("❌", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}


@Composable
private fun ResumenHabitos(
    total: Int,
    completados: Int
) {
    val progreso = if (total > 0) completados.toFloat() / total.toFloat() else 0f

    androidx.compose.material3.Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(42.dp)
            ) {
                val progresoAnimado by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = progreso,
                    animationSpec = androidx.compose.animation.core.tween(800),
                    label = "progresoHabitos"
                )
                androidx.compose.material3.CircularProgressIndicator(
                    progress = { progresoAnimado },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 4.dp,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text(
                    "${(progresoAnimado * 100).toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    "Hábitos de hoy",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "$completados de $total completados",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun BarraFiltrosHabitos(
    filtroActual: FiltroHabito,
    onFiltroSeleccionado: (FiltroHabito) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FiltroHabito.entries.forEach { filtro ->
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
