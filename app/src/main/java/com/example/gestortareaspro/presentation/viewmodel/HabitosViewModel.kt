package com.example.gestortareaspro.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gestortareaspro.domain.model.CategoriaHabito
import com.example.gestortareaspro.domain.model.Habito
import com.example.gestortareaspro.domain.model.HistorialHabito
import com.example.gestortareaspro.domain.repository.HabitoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class FiltroHabito(val etiqueta: String) {
    TODOS("Todos"),
    COMPLETADOS("Completados"),
    PENDIENTES("Pendientes")
}

/**
 * Estado inmutable de la pantalla de hábitos (lista).
 */
data class EstadoPantallaHabitos(
    val habitos: List<Habito> = emptyList(),
    val textoNuevoHabito: String = "",
    val descripcionNuevoHabito: String = "",
    val iconoNuevoHabito: String = "🎯",
    val categoriaNuevoHabito: CategoriaHabito = CategoriaHabito.OTRO,
    val filtroActual: FiltroHabito = FiltroHabito.TODOS,
    val totalHabitos: Int = 0,
    val habitosCompletadosHoy: Int = 0,
    val mostrarDialogoNuevo: Boolean = false
)

/**
 * Estados precisos del calendario por día.
 */
enum class EstadoDiaCalendario {
    COMPLETO,
    PARCIAL,
    FALLADO,
    SIN_REGISTRO
}

/**
 * Snapshot inmutable del progreso diario.
 */
data class DatosDiaCalendario(
    val completados: Int,
    val totalHabitos: Int,
    val porcentaje: Float,
    val estado: EstadoDiaCalendario
)

/**
 * Estado del calendario en la pantalla principal (Home).
 * Muestra resumen de completados por día de TODOS los hábitos.
 */
data class EstadoCalendarioHome(
    val mesSeleccionado: YearMonth = YearMonth.now(),
    val fechaSeleccionada: LocalDate? = null,
    val datosPorDia: Map<LocalDate, DatosDiaCalendario> = emptyMap(),
    val habitosDiaSeleccionado: List<HabitoDiaResumen> = emptyList(),
    val datosDiaSeleccionado: DatosDiaCalendario? = null
)

/**
 * Resumen de un hábito para un día específico del calendario.
 */
data class HabitoDiaResumen(
    val id: Int,
    val nombre: String,
    val icono: String,
    val completado: Boolean
)

/**
 * Estado reactivo de detalle de un hábito individual.
 */
data class EstadoDetalleHabito(
    val habito: Habito,
    val historialMes: List<HistorialHabito>,
    val totalDiasCompletados: Int,
    val mesSeleccionado: YearMonth,
    val fechasCompletadasMes: Set<LocalDate>
)

/**
 * ViewModel que gestiona todo el estado de hábitos:
 * - Lista filtrada (estadoUI)
 * - Calendario Home global (estadoCalendario)
 * - Detalle individual (obtenerEstadoDetalle)
 *
 * Todos los datos provienen de Room en tiempo real.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HabitosViewModel(
    private val repositorio: HabitoRepository
) : ViewModel() {

    private val _filtroActual = MutableStateFlow(FiltroHabito.TODOS)
    private val _textoNuevoHabito = MutableStateFlow("")
    private val _descripcionNuevoHabito = MutableStateFlow("")
    private val _iconoNuevoHabito = MutableStateFlow("🎯")
    private val _categoriaNuevoHabito = MutableStateFlow(CategoriaHabito.OTRO)

    // Estado del calendario Home
    private val _mesCalendarioHome = MutableStateFlow(YearMonth.now())
    private val _fechaSeleccionadaHome = MutableStateFlow<LocalDate?>(null)

    private val formatoFecha = DateTimeFormatter.ISO_LOCAL_DATE

            
    val estadoUI: StateFlow<EstadoPantallaHabitos> = combine(
        repositorio.obtenerTodos(),
        _filtroActual,
        _textoNuevoHabito,
        _descripcionNuevoHabito,
        _iconoNuevoHabito
    ) { habitos, filtro, texto, descripcion, icono ->
        val habitosFiltrados = when (filtro) {
            FiltroHabito.TODOS -> habitos
            FiltroHabito.COMPLETADOS -> habitos.filter { it.completadoHoy }
            FiltroHabito.PENDIENTES -> habitos.filter { !it.completadoHoy }
        }
        EstadoPantallaHabitos(
            habitos = habitosFiltrados,
            textoNuevoHabito = texto,
            descripcionNuevoHabito = descripcion,
            iconoNuevoHabito = icono,
            categoriaNuevoHabito = _categoriaNuevoHabito.value,
            filtroActual = filtro,
            totalHabitos = habitos.size,
            habitosCompletadosHoy = habitos.count { it.completadoHoy }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EstadoPantallaHabitos()
    )

            
    /**
     * Estado reactivo del calendario global.
     * Usa flatMapLatest para reaccionar al cambio de mes,
     * y combine interno para unir hábitos + historial.
     *
     * Cuando cambia el mes -> nueva query de historial.
     * Cuando cambia el día seleccionado -> recalcular lista de hábitos.
     * Cuando cambia el historial en Room -> actualización automática.
     */
    val estadoCalendario: StateFlow<EstadoCalendarioHome> = combine(
        _mesCalendarioHome,
        _fechaSeleccionadaHome
    ) { mes, fecha ->
        Pair(mes, fecha)
    }.flatMapLatest { (mes, fechaSeleccionada) ->
        val inicio = mes.atDay(1).format(formatoFecha)
        val fin = mes.atEndOfMonth().format(formatoFecha)

        combine(
            repositorio.obtenerTodos(),
            repositorio.obtenerTodoHistorialMes(inicio, fin)
        ) { habitos, historialMes ->
            // 1. Mapear hábitos con su fecha de creación para evaluar disponibilidad histórica
            val habitosConFecha = habitos.map { habito ->
                val fechaCreacion = Instant.ofEpochMilli(habito.fechaCreacion)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                habito to fechaCreacion
            }

            // 2. Agrupar completados por fecha (contar hábitos distintos por día)
            val historialAgrupado = historialMes
                .mapNotNull { registro ->
                    try {
                        LocalDate.parse(registro.fecha, formatoFecha) to registro.habitoId
                    } catch (e: Exception) {
                        null
                    }
                }
                .groupBy({ it.first }, { it.second })
                .mapValues { (_, ids) -> ids.distinct().size }

            // 3. Generar DatosDiaCalendario para todos los días del mes
            val hoy = LocalDate.now()
            val longitudMes = mes.lengthOfMonth()
            val datosPorDia = mutableMapOf<LocalDate, DatosDiaCalendario>()

            for (dia in 1..longitudMes) {
                val current = mes.atDay(dia)
                if (current.isAfter(hoy)) {
                    datosPorDia[current] = DatosDiaCalendario(0, 0, 0f, EstadoDiaCalendario.SIN_REGISTRO)
                    continue
                }

                val completados = historialAgrupado[current] ?: 0
                // Filtra los hábitos que no existían en la fecha evaluada para evitar falsos negativos históricos.
                val totalDia = habitosConFecha.count { it.second <= current }
                val porcentaje = if (totalDia > 0) (completados.toFloat() / totalDia.toFloat()) * 100f else 0f
                
                val estado = when {
                    totalDia == 0 -> EstadoDiaCalendario.SIN_REGISTRO
                    completados == totalDia -> EstadoDiaCalendario.COMPLETO
                    completados > 0 -> EstadoDiaCalendario.PARCIAL
                    else -> EstadoDiaCalendario.FALLADO
                }
                
                datosPorDia[current] = DatosDiaCalendario(completados, totalDia, porcentaje, estado)
            }

            // 4. Calcular lista de hábitos para el día seleccionado
            val habitosDia = if (fechaSeleccionada != null) {
                val fechaStr = fechaSeleccionada.format(formatoFecha)
                val idsCompletados = historialMes
                    .filter { it.fecha == fechaStr }
                    .map { it.habitoId }
                    .toSet()

                habitosConFecha
                    .filter { it.second <= fechaSeleccionada } // Solo hábitos que existían ese día
                    .map { (habito, _) ->
                        HabitoDiaResumen(
                            id = habito.id,
                            nombre = habito.nombre,
                            icono = habito.icono,
                            completado = idsCompletados.contains(habito.id)
                        )
                    }
            } else {
                emptyList()
            }

            val datosDiaSel = if (fechaSeleccionada != null) datosPorDia[fechaSeleccionada] else null

            EstadoCalendarioHome(
                mesSeleccionado = mes,
                fechaSeleccionada = fechaSeleccionada,
                datosPorDia = datosPorDia,
                habitosDiaSeleccionado = habitosDia,
                datosDiaSeleccionado = datosDiaSel
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EstadoCalendarioHome()
    )

            
    fun actualizarTextoNuevoHabito(texto: String) {
        _textoNuevoHabito.value = texto
    }

    fun actualizarDescripcionNuevoHabito(descripcion: String) {
        _descripcionNuevoHabito.value = descripcion
    }

    fun actualizarIconoNuevoHabito(icono: String) {
        _iconoNuevoHabito.value = icono
    }

    fun actualizarCategoriaNuevoHabito(categoria: CategoriaHabito) {
        _categoriaNuevoHabito.value = categoria
    }

    fun agregarHabito() {
        val nombre = _textoNuevoHabito.value.trim()
        if (nombre.isNotBlank()) {
            viewModelScope.launch {
                repositorio.insertar(
                    Habito(
                        nombre = nombre,
                        descripcion = _descripcionNuevoHabito.value.trim(),
                        icono = _iconoNuevoHabito.value,
                        categoria = _categoriaNuevoHabito.value
                    )
                )
                _textoNuevoHabito.value = ""
                _descripcionNuevoHabito.value = ""
                _iconoNuevoHabito.value = "🎯"
                _categoriaNuevoHabito.value = CategoriaHabito.OTRO
            }
        }
    }

    fun alternarCompletadoHoy(habito: Habito) {
        viewModelScope.launch {
            val hoy = LocalDate.now().format(formatoFecha)
            repositorio.alternarCompletadoHoy(habito.id, hoy)
        }
    }

    fun eliminarHabito(habito: Habito) {
        viewModelScope.launch {
            repositorio.eliminar(habito)
        }
    }

    fun seleccionarFiltro(filtro: FiltroHabito) {
        _filtroActual.value = filtro
    }

            
    fun mesCalendarioAnterior() {
        _mesCalendarioHome.value = _mesCalendarioHome.value.minusMonths(1)
        _fechaSeleccionadaHome.value = null // Reset selección al cambiar mes
    }

    fun mesCalendarioSiguiente() {
        val mesActual = YearMonth.now()
        if (_mesCalendarioHome.value.isBefore(mesActual)) {
            _mesCalendarioHome.value = _mesCalendarioHome.value.plusMonths(1)
            _fechaSeleccionadaHome.value = null
        }
    }

    fun seleccionarFechaCalendario(fecha: LocalDate?) {
        // Toggle: si ya está seleccionada, deseleccionar
        _fechaSeleccionadaHome.value = if (_fechaSeleccionadaHome.value == fecha) null else fecha
    }

            
    /**
     * Flow reactivo para la pantalla de detalle.
     * Combina: hábito + historial del mes + conteo total.
     */
    fun obtenerEstadoDetalle(
        habitoId: Int,
        mesSeleccionado: YearMonth = YearMonth.now()
    ): Flow<EstadoDetalleHabito?> {
        val primerDia = mesSeleccionado.atDay(1).format(formatoFecha)
        val ultimoDia = mesSeleccionado.atEndOfMonth().format(formatoFecha)

        return combine(
            repositorio.obtenerPorIdReactivo(habitoId),
            repositorio.obtenerHistorialEntreFechas(habitoId, primerDia, ultimoDia),
            repositorio.contarDiasCompletadosFlow(habitoId)
        ) { habito, historialMes, totalDias ->
            if (habito == null) return@combine null

            val fechasCompletadas = historialMes
                .filter { it.completado }
                .mapNotNull { registro ->
                    try {
                        LocalDate.parse(registro.fecha, formatoFecha)
                    } catch (e: Exception) {
                        null
                    }
                }
                .toSet()

            EstadoDetalleHabito(
                habito = habito,
                historialMes = historialMes,
                totalDiasCompletados = totalDias,
                mesSeleccionado = mesSeleccionado,
                fechasCompletadasMes = fechasCompletadas
            )
        }
    }
}

/**
 * Factory para crear HabitosViewModel con dependencias inyectadas.
 */
class HabitosViewModelFactory(
    private val repositorio: HabitoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitosViewModel::class.java)) {
            return HabitosViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
