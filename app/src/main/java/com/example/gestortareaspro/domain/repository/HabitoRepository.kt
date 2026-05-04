package com.example.gestortareaspro.domain.repository

import com.example.gestortareaspro.domain.model.Habito
import com.example.gestortareaspro.domain.model.HistorialHabito
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de hábitos.
 * La capa de dominio depende de esta abstracción,
 * NO de la implementación concreta (Principio DIP).
 */
interface HabitoRepository {
    fun obtenerTodos(): Flow<List<Habito>>
    fun obtenerHistorial(habitoId: Int): Flow<List<HistorialHabito>>
    fun obtenerHistorialEntreFechas(
        habitoId: Int,
        fechaInicio: String,
        fechaFin: String
    ): Flow<List<HistorialHabito>>
    fun obtenerPorIdReactivo(habitoId: Int): Flow<Habito?>
    fun contarDiasCompletadosFlow(habitoId: Int): Flow<Int>

    /** Historial de TODOS los hábitos en un rango (para calendario Home). */
    fun obtenerTodoHistorialMes(
        fechaInicio: String,
        fechaFin: String
    ): Flow<List<HistorialHabito>>

    suspend fun obtenerPorId(id: Int): Habito?
    suspend fun insertar(habito: Habito): Long
    suspend fun actualizar(habito: Habito)
    suspend fun eliminar(habito: Habito)
    suspend fun alternarCompletadoHoy(habitoId: Int, fecha: String)
    suspend fun estaCompletadoHoy(habitoId: Int, fecha: String): Boolean
    suspend fun calcularRacha(habitoId: Int): Int
    suspend fun contarDiasCompletados(habitoId: Int): Int
}
