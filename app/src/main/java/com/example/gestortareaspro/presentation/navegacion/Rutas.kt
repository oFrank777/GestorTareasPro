package com.example.gestortareaspro.presentation.navegacion

/**
 * Rutas de navegación de la aplicación.
 * Utiliza sealed class para garantizar exhaustividad
 * en el when y seguridad de tipos en la navegación.
 *
 * Flujo: Splash → Login (si no hay sesión) → Inicio → DetalleHabito
 */
sealed class Rutas(val ruta: String) {

    /** Pantalla de splash con animación de entrada. */
    object Splash : Rutas("splash")

    /** Pantalla de login/registro de nombre de usuario. */
    object Login : Rutas("login")

    /** Pantalla principal con pestañas (Tareas + Hábitos). */
    object Inicio : Rutas("inicio")

    /** Pantalla de detalle de un hábito específico. */
    object DetalleHabito : Rutas("detalle_habito/{habitoId}") {
        fun crearRuta(habitoId: Int) = "detalle_habito/$habitoId"
    }
}
