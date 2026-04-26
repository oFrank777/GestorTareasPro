package com.example.gestortareaspro.presentation.pantalla

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestortareaspro.domain.model.Tarea
import com.example.gestortareaspro.presentation.components.*
import com.example.gestortareaspro.presentation.viewmodel.EstadoPantallaTareas
import com.example.gestortareaspro.presentation.viewmodel.FiltroTarea

@Composable
fun PantallaTareas(
    estado: EstadoPantallaTareas,
    onTextoNuevaTareaCambiado: (String) -> Unit,
    onAgregarTarea: () -> Unit,
    onAlternarCompletada: (Tarea) -> Unit,
    onIniciarEdicion: (Tarea) -> Unit,
    onConfirmarEdicion: (String) -> Unit,
    onCancelarEdicion: () -> Unit,
    onEliminarTarea: (Tarea) -> Unit,
    onFiltroSeleccionado: (FiltroTarea) -> Unit,
    onAlternarTema: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            HeaderApp(
                totalTareas = estado.totalTareas,
                tareasCompletadas = estado.tareasCompletadas,
                temaOscuro = estado.temaOscuro,
                onAlternarTema = onAlternarTema
            )

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            BarraFiltros(
                filtroActual = estado.filtroActual,
                onFiltroSeleccionado = onFiltroSeleccionado
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animación fluida de la lista
            AnimatedContent(
                targetState = estado.tareas,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "listaAnimada",
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

    estado.tareaEditando?.let { tarea ->
        DialogoEdicion(
            tituloActual = tarea.titulo,
            onConfirmar = onConfirmarEdicion,
            onCancelar = onCancelarEdicion
        )
    }
}

