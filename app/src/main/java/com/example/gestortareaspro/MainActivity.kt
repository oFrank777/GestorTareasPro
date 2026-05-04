package com.example.gestortareaspro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.gestortareaspro.core.theme.GestorTareasProTema
import com.example.gestortareaspro.presentation.navegacion.GrafoNavegacion
import com.example.gestortareaspro.presentation.viewmodel.HabitosViewModel
import com.example.gestortareaspro.presentation.viewmodel.HabitosViewModelFactory
import com.example.gestortareaspro.presentation.viewmodel.TareasViewModel
import com.example.gestortareaspro.presentation.viewmodel.TareasViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Punto de entrada principal de la aplicación.
 * Configura la UI inyectando las dependencias base (ViewModels y DataStore)
 * al grafo de navegación.
 */
class MainActivity : ComponentActivity() {

    /** Scope para operaciones de DataStore aisladas de la UI. */
    private val alcanceActividad = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val aplicacion = application as GestorTareasApp
            val tareasViewModel: TareasViewModel = viewModel(
                factory = TareasViewModelFactory(
                    repositorio = aplicacion.repositorio,
                    preferencias = aplicacion.preferencias
                )
            )

            val habitosViewModel: HabitosViewModel = viewModel(
                factory = HabitosViewModelFactory(
                    repositorio = aplicacion.repositorioHabitos
                )
            )
            val estadoTareas by tareasViewModel.estadoUI
                .collectAsStateWithLifecycle()
                        val nombreUsuario by aplicacion.preferencias.nombreUsuario
                .collectAsStateWithLifecycle(initialValue = "")
            val controladorNav = rememberNavController()
            GestorTareasProTema(temaOscuro = estadoTareas.temaOscuro) {
                GrafoNavegacion(
                    controladorNav = controladorNav,
                    tareasViewModel = tareasViewModel,
                    habitosViewModel = habitosViewModel,
                    preferencias = aplicacion.preferencias,
                    alcanceCoroutines = alcanceActividad,
                    nombreUsuario = nombreUsuario,
                    temaOscuro = estadoTareas.temaOscuro,
                    onAlternarTema = tareasViewModel::alternarTema
                )
            }
        }
    }
}
