package com.example.gestortareaspro.data.repository

import com.example.gestortareaspro.data.local.HabitoDao
import com.example.gestortareaspro.data.local.HabitoEntidad
import com.example.gestortareaspro.data.local.HistorialHabitoEntidad
import com.example.gestortareaspro.domain.model.Habito
import com.example.gestortareaspro.domain.model.HistorialHabito
import com.example.gestortareaspro.domain.repository.HabitoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Implementación concreta del repositorio de hábitos.
 * Gestiona persistencia con Room, cálculo real de rachas
 * basado en historial de fechas consecutivas, y consultas
 * reactivas para el calendario y estadísticas.
 */
class HabitoRepositoryImpl(
    private val habitoDao: HabitoDao
) : HabitoRepository {

    private val formatoFecha = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Obtiene todos los hábitos con su estado de completado
     * para el día actual y rachas recalculadas en tiempo real.
     */
    override fun obtenerTodos(): Flow<List<Habito>> {
        return habitoDao.obtenerTodos().map { entidades ->
            val hoy = LocalDate.now().format(formatoFecha)
            entidades.map { entidad ->
                val completadoHoy = habitoDao.obtenerRegistroDiario(entidad.id, hoy)
                    ?.completado ?: false
                // Recalcular rachas en cada lectura para consistencia
                val rachaReal = calcularRachaReal(entidad.id)
                val mejorRachaReal = maxOf(entidad.mejorRacha, rachaReal)
                entidad.aModelo(completadoHoy = completadoHoy).copy(
                    rachaActual = rachaReal,
                    mejorRacha = mejorRachaReal
                )
            }
        }
    }

    override fun obtenerHistorial(habitoId: Int): Flow<List<HistorialHabito>> {
        return habitoDao.obtenerHistorial(habitoId).map { entidades ->
            entidades.map { it.aModelo() }
        }
    }

    /**
     * Obtiene historial entre dos fechas para el calendario de detalle.
     */
    override fun obtenerHistorialEntreFechas(
        habitoId: Int,
        fechaInicio: String,
        fechaFin: String
    ): Flow<List<HistorialHabito>> {
        return habitoDao.obtenerHistorialEntreFechas(habitoId, fechaInicio, fechaFin)
            .map { entidades -> entidades.map { it.aModelo() } }
    }

    /**
     * Obtiene historial global de TODOS los hábitos en un rango de fechas.
     * Usado para el calendario de la pantalla principal (Home).
     */
    override fun obtenerTodoHistorialMes(
        fechaInicio: String,
        fechaFin: String
    ): Flow<List<HistorialHabito>> {
        return habitoDao.obtenerTodoHistorialMes(fechaInicio, fechaFin)
            .map { entidades -> entidades.map { it.aModelo() } }
    }

    override suspend fun obtenerPorId(id: Int): Habito? {
        val entidad = habitoDao.obtenerPorId(id) ?: return null
        val hoy = LocalDate.now().format(formatoFecha)
        val completadoHoy = habitoDao.obtenerRegistroDiario(id, hoy)
            ?.completado ?: false
        val rachaReal = calcularRachaReal(id)
        val mejorRachaReal = maxOf(entidad.mejorRacha, rachaReal)
        return entidad.aModelo(completadoHoy = completadoHoy).copy(
            rachaActual = rachaReal,
            mejorRacha = mejorRachaReal
        )
    }

    /**
     * Flow reactivo del hábito individual.
     * Se actualiza automáticamente cuando cambia el historial.
     */
    override fun obtenerPorIdReactivo(habitoId: Int): Flow<Habito?> {
        return combine(
            habitoDao.obtenerPorIdFlow(habitoId),
            habitoDao.obtenerHistorial(habitoId)
        ) { entidad, _ ->
            if (entidad == null) return@combine null
            val hoy = LocalDate.now().format(formatoFecha)
            val completadoHoy = habitoDao.obtenerRegistroDiario(entidad.id, hoy)
                ?.completado ?: false
            val rachaReal = calcularRachaReal(entidad.id)
            val mejorRachaReal = maxOf(entidad.mejorRacha, rachaReal)
            entidad.aModelo(completadoHoy = completadoHoy).copy(
                rachaActual = rachaReal,
                mejorRacha = mejorRachaReal
            )
        }
    }

    override fun contarDiasCompletadosFlow(habitoId: Int): Flow<Int> {
        return habitoDao.contarDiasCompletadosFlow(habitoId)
    }

    override suspend fun insertar(habito: Habito): Long {
        return habitoDao.insertar(HabitoEntidad.desdeModelo(habito))
    }

    override suspend fun actualizar(habito: Habito) {
        habitoDao.actualizar(HabitoEntidad.desdeModelo(habito))
    }

    override suspend fun eliminar(habito: Habito) {
        habitoDao.eliminar(HabitoEntidad.desdeModelo(habito))
    }

    /**
     * Alterna el estado de completado de un hábito para una fecha.
     * Si está completado → lo desmarca (elimina registro).
     * Si está pendiente → lo marca como completado.
     * Después recalcula y persiste las rachas actualizadas.
     */
    override suspend fun alternarCompletadoHoy(habitoId: Int, fecha: String) {
        val registroExistente = habitoDao.obtenerRegistroDiario(habitoId, fecha)
        if (registroExistente != null && registroExistente.completado) {
            // Desmarcar: eliminar el registro completado
            habitoDao.eliminarRegistroDiario(habitoId, fecha)
        } else {
            // Marcar como completado (insertar o actualizar)
            habitoDao.insertarRegistro(
                HistorialHabitoEntidad(
                    habitoId = habitoId,
                    fecha = fecha,
                    completado = true
                )
            )
        }
        // Persistir rachas recalculadas en la entidad
        persistirRachas(habitoId)
    }

    override suspend fun estaCompletadoHoy(habitoId: Int, fecha: String): Boolean {
        return habitoDao.obtenerRegistroDiario(habitoId, fecha)?.completado ?: false
    }

    /**
     * CÁLCULO REAL DE RACHAS basado en fechas consecutivas.
     *
     * Algoritmo:
     * 1. Obtener todas las fechas completadas ordenadas descendentemente
     * 2. Si hoy está completado → empezar desde hoy
     *    Si no → empezar desde ayer (la racha puede estar "viva" aún)
     * 3. Recorrer hacia atrás contando días consecutivos
     * 4. Romper la racha al encontrar un hueco
     */
    override suspend fun calcularRacha(habitoId: Int): Int {
        return calcularRachaReal(habitoId)
    }

    override suspend fun contarDiasCompletados(habitoId: Int): Int {
        return habitoDao.contarDiasCompletados(habitoId)
    }

    /**
     * Lógica central de cálculo de rachas.
     * Separada como función privada para reutilización interna.
     */
    private suspend fun calcularRachaReal(habitoId: Int): Int {
        val diasCompletados = habitoDao.obtenerDiasCompletados(habitoId)
        if (diasCompletados.isEmpty()) return 0

        // Convertir strings a LocalDate y ordenar descendentemente
        val fechasOrdenadas = diasCompletados
            .mapNotNull { registro ->
                try {
                    LocalDate.parse(registro.fecha, formatoFecha)
                } catch (e: Exception) {
                    null // Ignorar fechas mal formateadas
                }
            }
            .distinct()
            .sortedDescending()

        if (fechasOrdenadas.isEmpty()) return 0

        val hoy = LocalDate.now()
        var racha = 0

        // Determinar punto de inicio:
        // Si hoy completado → empezar desde hoy
        // Si ayer completado → la racha sigue viva desde ayer
        // Si ninguno → racha es 0
        val fechaMasReciente = fechasOrdenadas.first()
        val fechaEsperada: LocalDate = when (fechaMasReciente) {
            hoy -> hoy
            hoy.minusDays(1) -> hoy.minusDays(1)
            else -> return 0 // Sin actividad reciente, racha rota
        }

        // Contar días consecutivos hacia atrás
        var diaActual = fechaEsperada
        for (fecha in fechasOrdenadas) {
            if (fecha == diaActual) {
                racha++
                diaActual = diaActual.minusDays(1)
            } else if (fecha.isBefore(diaActual)) {
                // Hueco encontrado → racha rota
                break
            }
        }

        return racha
    }

    /**
     * Persiste las rachas recalculadas en la tabla de hábitos.
     * Actualiza rachaActual y mejorRacha en la entidad Room.
     */
    private suspend fun persistirRachas(habitoId: Int) {
        val rachaActual = calcularRachaReal(habitoId)
        val entidad = habitoDao.obtenerPorId(habitoId) ?: return
        val mejorRacha = maxOf(entidad.mejorRacha, rachaActual)
        habitoDao.actualizar(
            entidad.copy(
                rachaActual = rachaActual,
                mejorRacha = mejorRacha
            )
        )
    }
}
