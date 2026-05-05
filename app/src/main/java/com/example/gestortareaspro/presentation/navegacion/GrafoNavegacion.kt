package com.example.gestortareaspro.presentation.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gestortareaspro.data.local.PreferenciasManager
import com.example.gestortareaspro.presentation.pantalla.PantallaDetalle
import com.example.gestortareaspro.presentation.pantalla.PantallaInicio
import com.example.gestortareaspro.presentation.pantalla.PantallaLogin
import com.example.gestortareaspro.presentation.pantalla.PantallaSplash
import com.example.gestortareaspro.presentation.viewmodel.HabitosViewModel
import com.example.gestortareaspro.presentation.viewmodel.TareasViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.YearMonth

/**
 * Grafo principal de navegación.
 * Define las rutas y dependencias inyectadas para cada pantalla.
 */
@Composable
fun GrafoNavegacion(
    controladorNav: NavHostController,
    tareasViewModel: TareasViewModel,
    habitosViewModel: HabitosViewModel,
    preferencias: PreferenciasManager,
    alcanceCoroutines: CoroutineScope,
    nombreUsuario: String,
    temaOscuro: Boolean,
    onAlternarTema: () -> Unit
) {
    NavHost(
        navController = controladorNav,
        startDestination = Rutas.Splash.ruta
    ) {
        composable(route = Rutas.Splash.ruta) {
            PantallaSplash(
                flujoSesion = preferencias.sesionIniciada,
                onNavegar = { sesionActiva ->
                    val destino = if (sesionActiva) {
                        Rutas.Inicio.ruta
                    } else {
                        Rutas.Login.ruta
                    }
                    controladorNav.navigate(destino) {
                        popUpTo(Rutas.Splash.ruta) { inclusive = true }
                    }
                }
            )
        }

                composable(route = Rutas.Login.ruta) {
            PantallaLogin(
                onIniciarSesion = { nombre ->
                    alcanceCoroutines.launch {
                        preferencias.iniciarSesion(nombre)
                    }
                    controladorNav.navigate(Rutas.Inicio.ruta) {
                        popUpTo(Rutas.Login.ruta) { inclusive = true }
                    }
                }
            )
        }

                composable(route = Rutas.Inicio.ruta) {
            val estadoTareas by tareasViewModel.estadoUI
                .collectAsStateWithLifecycle()
            val estadoHabitos by habitosViewModel.estadoUI
                .collectAsStateWithLifecycle()
            val estadoCalendario by habitosViewModel.estadoCalendario
                .collectAsStateWithLifecycle()

            PantallaInicio(
                estadoTareas = estadoTareas,
                estadoHabitos = estadoHabitos,
                estadoCalendario = estadoCalendario,
                nombreUsuario = nombreUsuario,
                temaOscuro = temaOscuro,
                onAlternarTema = onAlternarTema,
                // Callbacks de tareas
                onTextoNuevaTareaCambiado = tareasViewModel::actualizarTextoNuevaTarea,
                onAgregarTarea = tareasViewModel::agregarTarea,
                onAlternarCompletada = tareasViewModel::alternarCompletada,
                onIniciarEdicion = tareasViewModel::iniciarEdicion,
                onConfirmarEdicion = tareasViewModel::confirmarEdicion,
                onCancelarEdicion = tareasViewModel::cancelarEdicion,
                onEliminarTarea = tareasViewModel::eliminarTarea,
                onFiltroTareaSeleccionado = tareasViewModel::seleccionarFiltro,
                // Callbacks de hábitos
                onTextoNuevoHabitoCambiado = habitosViewModel::actualizarTextoNuevoHabito,
                onDescripcionNuevoHabitoCambiada = habitosViewModel::actualizarDescripcionNuevoHabito,
                onIconoNuevoHabitoSeleccionado = habitosViewModel::actualizarIconoNuevoHabito,
                onCategoriaNuevoHabitoSeleccionada = habitosViewModel::actualizarCategoriaNuevoHabito,
                onAgregarHabito = habitosViewModel::agregarHabito,
                onAlternarHabitoCompletado = habitosViewModel::alternarCompletadoHoy,
                onEliminarHabito = habitosViewModel::eliminarHabito,
                onFiltroHabitoSeleccionado = habitosViewModel::seleccionarFiltro,
                // Callbacks del calendario Home
                onMesCalendarioAnterior = habitosViewModel::mesCalendarioAnterior,
                onMesCalendarioSiguiente = habitosViewModel::mesCalendarioSiguiente,
                onFechaCalendarioSeleccionada = habitosViewModel::seleccionarFechaCalendario,
                // Navegación
                onNavegarDetalleHabito = { habitoId ->
                    controladorNav.navigate(Rutas.DetalleHabito.crearRuta(habitoId))
                },
                onCerrarSesion = {
                    alcanceCoroutines.launch {
                        preferencias.cerrarSesion()
                        controladorNav.navigate(Rutas.Login.ruta) {
                            popUpTo(Rutas.Inicio.ruta) { inclusive = true }
                        }
                    }
                }
            )
        }

                composable(
            route = Rutas.DetalleHabito.ruta,
            arguments = listOf(
                navArgument("habitoId") { type = NavType.IntType }
            )
        ) { entrada ->
            val habitoId = entrada.arguments?.getInt("habitoId") ?: return@composable

            // Estado local del mes para el calendario de detalle
            var mesSeleccionado by remember { mutableStateOf(YearMonth.now()) }

            val estadoDetalle by habitosViewModel
                .obtenerEstadoDetalle(habitoId, mesSeleccionado)
                .collectAsStateWithLifecycle(initialValue = null)

            estadoDetalle?.let { detalle ->
                PantallaDetalle(
                    habito = detalle.habito,
                    totalDiasCompletados = detalle.totalDiasCompletados,
                    mesSeleccionado = detalle.mesSeleccionado,
                    fechasCompletadasMes = detalle.fechasCompletadasMes,
                    onVolverAtras = {
                        // popBackStack() vuelve a la pantalla anterior
                        // que SIEMPRE es Inicio (con la pestaña Hábitos activa
                        // gracias a rememberSaveable en PantallaInicio)
                        controladorNav.popBackStack()
                    },
                    onAlternarCompletado = {
                        habitosViewModel.alternarCompletadoHoy(detalle.habito)
                    },
                    onMesAnterior = {
                        mesSeleccionado = mesSeleccionado.minusMonths(1)
                    },
                    onMesSiguiente = {
                        if (mesSeleccionado.isBefore(YearMonth.now())) {
                            mesSeleccionado = mesSeleccionado.plusMonths(1)
                        }
                    }
                )
            }
        }
    }
}
