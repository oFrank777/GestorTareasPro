// ============================================================
// GestorTareasApp.kt — Clase Application personalizada
// Inicializa las dependencias globales (Room + Preferencias)
// ============================================================
package com.example.gestortareaspro

import android.app.Application
import com.example.gestortareaspro.data.local.PreferenciasManager
import com.example.gestortareaspro.data.local.TareaDatabase
import com.example.gestortareaspro.data.repository.TareaRepositoryImpl
import com.example.gestortareaspro.domain.repository.TareaRepository

/**
 * Application personalizada que actúa como contenedor
 * de dependencias (Service Locator simple).
 * Inicializa Room y el repositorio una única vez.
 */
class GestorTareasApp : Application() {

    /** Base de datos Room (inicialización perezosa). */
    val baseDatos: TareaDatabase by lazy {
        TareaDatabase.obtenerInstancia(this)
    }

    /** Repositorio de tareas (capa de abstracción sobre Room). */
    val repositorio: TareaRepository by lazy {
        TareaRepositoryImpl(baseDatos.tareaDao())
    }

    /** Manager de preferencias con DataStore. */
    val preferencias: PreferenciasManager by lazy {
        PreferenciasManager(this)
    }
}
