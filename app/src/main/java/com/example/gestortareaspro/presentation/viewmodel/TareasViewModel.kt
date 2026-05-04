package com.example.gestortareaspro.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gestortareaspro.data.local.PreferenciasManager
import com.example.gestortareaspro.domain.model.Tarea
import com.example.gestortareaspro.domain.repository.TareaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FiltroTarea(val etiqueta: String) {
    TODAS("Todas"),
    PENDIENTES("Pendientes"),
    COMPLETADAS("Completadas")
}

/**
 * Estado inmutable de la pantalla principal.
 * Contiene toda la información que la UI necesita para renderizarse.
 */
data class EstadoPantallaTareas(
    val tareas: List<Tarea> = emptyList(),
    val textoNuevaTarea: String = "",
    val filtroActual: FiltroTarea = FiltroTarea.TODAS,
    val temaOscuro: Boolean = false,
    val tareaEditando: Tarea? = null,
    val totalTareas: Int = 0,
    val tareasCompletadas: Int = 0
)

/**
 * ViewModel que gestiona el estado de la pantalla de tareas.
 * Combina el Flow de Room con el filtro seleccionado para
 * producir un único StateFlow observable por la UI.
 */
class TareasViewModel(
    private val repositorio: TareaRepository,
    private val preferencias: PreferenciasManager
) : ViewModel() {

    private val _filtroActual = MutableStateFlow(FiltroTarea.TODAS)
    private val _textoNuevaTarea = MutableStateFlow("")
    private val _tareaEditando = MutableStateFlow<Tarea?>(null)

    /**
     * Estado combinado que reacciona a cambios en:
     * - Lista de tareas (Room Flow)
     * - Filtro seleccionado
     * - Texto del campo de entrada
     * - Preferencia de tema
     * - Tarea en edición
     */
    val estadoUI: StateFlow<EstadoPantallaTareas> = combine(
        repositorio.obtenerTodas(),
        _filtroActual,
        _textoNuevaTarea,
        preferencias.esTemaOscuro,
        _tareaEditando
    ) { tareas, filtro, texto, temaOscuro, editando ->
        val tareasFiltradas = when (filtro) {
            FiltroTarea.TODAS -> tareas
            FiltroTarea.PENDIENTES -> tareas.filter { !it.completada }
            FiltroTarea.COMPLETADAS -> tareas.filter { it.completada }
        }
        EstadoPantallaTareas(
            tareas = tareasFiltradas,
            textoNuevaTarea = texto,
            filtroActual = filtro,
            temaOscuro = temaOscuro,
            tareaEditando = editando,
            totalTareas = tareas.size,
            tareasCompletadas = tareas.count { it.completada }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EstadoPantallaTareas()
    )

    
    fun actualizarTextoNuevaTarea(texto: String) {
        _textoNuevaTarea.value = texto
    }

    fun agregarTarea() {
        val titulo = _textoNuevaTarea.value.trim()
        if (titulo.isNotBlank()) {
            viewModelScope.launch {
                repositorio.insertar(Tarea(titulo = titulo))
                _textoNuevaTarea.value = ""
            }
        }
    }

    fun alternarCompletada(tarea: Tarea) {
        viewModelScope.launch {
            repositorio.actualizar(tarea.copy(completada = !tarea.completada))
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            repositorio.eliminar(tarea)
        }
    }

    fun seleccionarFiltro(filtro: FiltroTarea) {
        _filtroActual.value = filtro
    }

    fun iniciarEdicion(tarea: Tarea) {
        _tareaEditando.value = tarea
    }

    fun confirmarEdicion(nuevoTitulo: String) {
        val tarea = _tareaEditando.value ?: return
        viewModelScope.launch {
            repositorio.actualizar(tarea.copy(titulo = nuevoTitulo.trim()))
            _tareaEditando.value = null
        }
    }

    fun cancelarEdicion() {
        _tareaEditando.value = null
    }

    fun alternarTema() {
        viewModelScope.launch {
            val estadoActual = estadoUI.value.temaOscuro
            preferencias.guardarTemaOscuro(!estadoActual)
        }
    }
}

/**
 * Factory para crear el ViewModel con dependencias inyectadas.
 * Necesario porque TareasViewModel tiene parámetros en el constructor.
 */
class TareasViewModelFactory(
    private val repositorio: TareaRepository,
    private val preferencias: PreferenciasManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TareasViewModel::class.java)) {
            return TareasViewModel(repositorio, preferencias) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
