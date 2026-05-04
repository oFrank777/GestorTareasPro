package com.example.gestortareaspro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos Room que gestiona todas las tablas de la app.
 * Implementa el patrón Singleton para garantizar una
 * única instancia en toda la aplicación.
 *
 * Entidades:
 * - [TareaEntity]: Tabla de tareas
 * - [HabitoEntidad]: Tabla de hábitos (con categoría)
 * - [HistorialHabitoEntidad]: Tabla de historial diario de hábitos
 *
 * Versión 3: Agrega columna "categoria" a la tabla habitos.
 */
@Database(
    entities = [
        TareaEntity::class,
        HabitoEntidad::class,
        HistorialHabitoEntidad::class
    ],
    version = 3,
    exportSchema = false
)
abstract class TareaDatabase : RoomDatabase() {

    abstract fun tareaDao(): TareaDao
    abstract fun habitoDao(): HabitoDao

    companion object {
        @Volatile
        private var INSTANCIA: TareaDatabase? = null

        /**
         * Obtiene la instancia única de la base de datos.
         * Utiliza doble verificación de bloqueo (double-check
         * locking) para thread-safety sin overhead excesivo.
         */
        fun obtenerInstancia(contexto: Context): TareaDatabase {
            return INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    TareaDatabase::class.java,
                    "gestor_tareas_pro.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCIA = it }
            }
        }
    }
}
