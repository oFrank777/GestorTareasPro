// ============================================================
// TareaDatabase.kt — Base de datos Room (Data Layer)
// Singleton thread-safe con doble verificación de bloqueo
// ============================================================
package com.example.gestortareaspro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos Room que gestiona la tabla "tareas".
 * Implementa el patrón Singleton para garantizar una
 * única instancia en toda la aplicación.
 */
@Database(
    entities = [TareaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TareaDatabase : RoomDatabase() {

    abstract fun tareaDao(): TareaDao

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
