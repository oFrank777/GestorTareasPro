// ============================================================
// PreferenciasManager.kt — DataStore para preferencias
// Gestiona la persistencia del tema claro/oscuro
// ============================================================
package com.example.gestortareaspro.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Extensión que crea el DataStore en el contexto de la app. */
private val Context.almacenPreferencias by preferencesDataStore(
    name = "ajustes_gestor_tareas"
)

/**
 * Manager centralizado para preferencias de usuario.
 * Utiliza Jetpack DataStore (reemplazo moderno de SharedPreferences)
 * con API basada en Flow y coroutines.
 */
class PreferenciasManager(private val contexto: Context) {

    companion object {
        private val CLAVE_TEMA_OSCURO = booleanPreferencesKey("tema_oscuro")
    }

    /** Flow reactivo que emite true si el tema oscuro está activo. */
    val esTemaOscuro: Flow<Boolean> = contexto.almacenPreferencias.data
        .map { preferencias ->
            preferencias[CLAVE_TEMA_OSCURO] ?: false
        }

    /** Guarda la preferencia de tema oscuro de forma asíncrona. */
    suspend fun guardarTemaOscuro(esOscuro: Boolean) {
        contexto.almacenPreferencias.edit { preferencias ->
            preferencias[CLAVE_TEMA_OSCURO] = esOscuro
        }
    }
}
