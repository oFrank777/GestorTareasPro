// ============================================================
// TareaRepository.kt — Contrato del repositorio (Domain)
// Define las operaciones sin conocer la implementación
// ============================================================
package com.example.gestortareaspro.domain.repository

import com.example.gestortareaspro.domain.model.Tarea
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de tareas.
 * La capa de dominio depende de esta abstracción,
 * NO de la implementación concreta (Principio DIP).
 */
interface TareaRepository {
    fun obtenerTodas(): Flow<List<Tarea>>
    suspend fun insertar(tarea: Tarea)
    suspend fun actualizar(tarea: Tarea)
    suspend fun eliminar(tarea: Tarea)
}
