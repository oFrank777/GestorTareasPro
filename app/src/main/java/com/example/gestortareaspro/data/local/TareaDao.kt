package com.example.gestortareaspro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO de Room para la tabla "tareas".
 * - obtenerTodas() retorna un Flow reactivo que emite
 *   automáticamente cuando la tabla cambia.
 * - Las operaciones de escritura son suspend functions
 *   para ejecutarse en coroutines sin bloquear el UI thread.
 */
@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas ORDER BY completada ASC, id DESC")
    fun obtenerTodas(): Flow<List<TareaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(tarea: TareaEntity)

    @Update
    suspend fun actualizar(tarea: TareaEntity)

    @Delete
    suspend fun eliminar(tarea: TareaEntity)
}
