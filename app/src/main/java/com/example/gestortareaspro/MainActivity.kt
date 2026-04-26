// ============================================================
// MainActivity.kt — Punto de entrada de la aplicación
// Conecta el ViewModel con la pantalla principal
// ============================================================
package com.example.gestortareaspro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestortareaspro.core.theme.GestorTareasProTema
import com.example.gestortareaspro.presentation.pantalla.PantallaTareas
import com.example.gestortareaspro.presentation.viewmodel.TareasViewModel
import com.example.gestortareaspro.presentation.viewmodel.TareasViewModelFactory

/**
 * Activity principal y único punto de entrada.
 * Configura edge-to-edge, inyecta el ViewModel con su Factory,
 * y delega toda la UI a PantallaTareas.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Obtener dependencias desde Application
            val aplicacion = application as GestorTareasApp

            // Crear ViewModel con Factory (inyección manual)
            val viewModel: TareasViewModel = viewModel(
                factory = TareasViewModelFactory(
                    repositorio = aplicacion.repositorio,
                    preferencias = aplicacion.preferencias
                )
            )

            // Observar el estado reactivo (lifecycle-aware)
            val estado by viewModel.estadoUI
                .collectAsStateWithLifecycle()

            // Aplicar tema dinámico (claro/oscuro)
            GestorTareasProTema(temaOscuro = estado.temaOscuro) {
                PantallaTareas(
                    estado = estado,
                    onTextoNuevaTareaCambiado = viewModel::actualizarTextoNuevaTarea,
                    onAgregarTarea = viewModel::agregarTarea,
                    onAlternarCompletada = viewModel::alternarCompletada,
                    onIniciarEdicion = viewModel::iniciarEdicion,
                    onConfirmarEdicion = viewModel::confirmarEdicion,
                    onCancelarEdicion = viewModel::cancelarEdicion,
                    onEliminarTarea = viewModel::eliminarTarea,
                    onFiltroSeleccionado = viewModel::seleccionarFiltro,
                    onAlternarTema = viewModel::alternarTema
                )
            }
        }
    }
}
