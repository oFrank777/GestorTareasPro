package com.example.gestortareaspro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO de Room para las tablas "habitos" e "historial_habitos".
 * Proporciona acceso reactivo mediante Flow y operaciones
 * suspendidas para escritura segura en coroutines.
 */
@Dao
interface HabitoDao {

    
    @Query("SELECT * FROM habitos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<HabitoEntidad>>

    @Query("SELECT * FROM habitos WHERE id = :id")
    fun obtenerPorIdFlow(id: Int): Flow<HabitoEntidad?>

    @Query("SELECT * FROM habitos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): HabitoEntidad?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(habito: HabitoEntidad): Long

    @Update
    suspend fun actualizar(habito: HabitoEntidad)

    @Delete
    suspend fun eliminar(habito: HabitoEntidad)

    
    @Query("SELECT * FROM historial_habitos WHERE habito_id = :habitoId ORDER BY fecha DESC")
    fun obtenerHistorial(habitoId: Int): Flow<List<HistorialHabitoEntidad>>

    @Query("SELECT * FROM historial_habitos WHERE habito_id = :habitoId AND fecha = :fecha LIMIT 1")
    suspend fun obtenerRegistroDiario(habitoId: Int, fecha: String): HistorialHabitoEntidad?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRegistro(registro: HistorialHabitoEntidad)

    @Query("""
        SELECT * FROM historial_habitos 
        WHERE habito_id = :habitoId AND completado = 1 
        ORDER BY fecha DESC
    """)
    suspend fun obtenerDiasCompletados(habitoId: Int): List<HistorialHabitoEntidad>

    @Query("SELECT COUNT(*) FROM historial_habitos WHERE habito_id = :habitoId AND completado = 1")
    suspend fun contarDiasCompletados(habitoId: Int): Int

    @Query("SELECT COUNT(*) FROM historial_habitos WHERE habito_id = :habitoId AND completado = 1")
    fun contarDiasCompletadosFlow(habitoId: Int): Flow<Int>

    @Query("DELETE FROM historial_habitos WHERE habito_id = :habitoId AND fecha = :fecha")
    suspend fun eliminarRegistroDiario(habitoId: Int, fecha: String)

    @Query("""
        SELECT * FROM historial_habitos 
        WHERE habito_id = :habitoId 
        AND fecha BETWEEN :fechaInicio AND :fechaFin
        ORDER BY fecha ASC
    """)
    fun obtenerHistorialEntreFechas(
        habitoId: Int,
        fechaInicio: String,
        fechaFin: String
    ): Flow<List<HistorialHabitoEntidad>>

    
    /**
     * Obtiene todo el historial completado de TODOS los hábitos
     * dentro de un rango de fechas. Usado para el calendario
     * global en la pantalla principal (Home).
     */
    @Query("""
        SELECT * FROM historial_habitos 
        WHERE fecha BETWEEN :fechaInicio AND :fechaFin 
        AND completado = 1
        ORDER BY fecha ASC
    """)
    fun obtenerTodoHistorialMes(
        fechaInicio: String,
        fechaFin: String
    ): Flow<List<HistorialHabitoEntidad>>

    /**
     * Obtiene el historial de TODOS los hábitos para una fecha
     * específica. Usado cuando el usuario selecciona un día
     * en el calendario de la pantalla principal.
     */
    @Query("""
        SELECT * FROM historial_habitos 
        WHERE fecha = :fecha
        ORDER BY habito_id ASC
    """)
    fun obtenerHistorialPorFecha(fecha: String): Flow<List<HistorialHabitoEntidad>>
}
