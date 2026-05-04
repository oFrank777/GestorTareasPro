package com.example.gestortareaspro

import android.app.Application
import com.example.gestortareaspro.data.local.PreferenciasManager
import com.example.gestortareaspro.data.local.TareaDatabase
import com.example.gestortareaspro.data.repository.HabitoRepositoryImpl
import com.example.gestortareaspro.data.repository.TareaRepositoryImpl
import com.example.gestortareaspro.domain.repository.HabitoRepository
import com.example.gestortareaspro.domain.repository.TareaRepository

/**
 * Application personalizada que actúa como contenedor
 * de dependencias (Service Locator simple).
 * Inicializa Room y los repositorios una única vez.
 */
class GestorTareasApp : Application() {

    /** Base de datos Room (inicialización perezosa). */
    val baseDatos: TareaDatabase by lazy {
        TareaDatabase.obtenerInstancia(this)
    }

        val repositorio: TareaRepository by lazy {
        TareaRepositoryImpl(baseDatos.tareaDao())
    }

        val repositorioHabitos: HabitoRepository by lazy {
        HabitoRepositoryImpl(baseDatos.habitoDao())
    }

    /** Manager de preferencias con DataStore. */
    val preferencias: PreferenciasManager by lazy {
        PreferenciasManager(this)
    }
}
