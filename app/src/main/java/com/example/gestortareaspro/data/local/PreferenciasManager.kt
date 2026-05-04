
package com.example.gestortareaspro.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.almacenPreferencias by preferencesDataStore(
    name = "ajustes_gestor_tareas"
)

/**
 * Administrador de preferencias mediante DataStore.
 * Maneja el estado global de la aplicación (tema, sesión, nombre).
 */
class PreferenciasManager(private val contexto: Context) {

    companion object {
        private val CLAVE_TEMA_OSCURO = booleanPreferencesKey("tema_oscuro")
        private val CLAVE_NOMBRE_USUARIO = stringPreferencesKey("nombre_usuario")
        private val CLAVE_SESION_INICIADA = booleanPreferencesKey("sesion_iniciada")
    }


    val esTemaOscuro: Flow<Boolean> = contexto.almacenPreferencias.data
        .map { preferencias ->
            preferencias[CLAVE_TEMA_OSCURO] ?: false
        }


    suspend fun guardarTemaOscuro(esOscuro: Boolean) {
        contexto.almacenPreferencias.edit { preferencias ->
            preferencias[CLAVE_TEMA_OSCURO] = esOscuro
        }
    }


    val nombreUsuario: Flow<String> = contexto.almacenPreferencias.data
        .map { preferencias ->
            preferencias[CLAVE_NOMBRE_USUARIO] ?: ""
        }


    suspend fun guardarNombreUsuario(nombre: String) {
        contexto.almacenPreferencias.edit { preferencias ->
            preferencias[CLAVE_NOMBRE_USUARIO] = nombre
        }
    }


    val sesionIniciada: Flow<Boolean> = contexto.almacenPreferencias.data
        .map { preferencias ->
            preferencias[CLAVE_SESION_INICIADA] ?: false
        }


    suspend fun iniciarSesion(nombre: String) {
        contexto.almacenPreferencias.edit { preferencias ->
            preferencias[CLAVE_NOMBRE_USUARIO] = nombre
            preferencias[CLAVE_SESION_INICIADA] = true
        }
    }


    suspend fun cerrarSesion() {
        contexto.almacenPreferencias.edit { preferencias ->
            preferencias[CLAVE_SESION_INICIADA] = false
            preferencias[CLAVE_NOMBRE_USUARIO] = ""
        }
    }
}
