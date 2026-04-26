package com.example.gestortareaspro.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = IndigoPrimario,
    onPrimary = Color.White,
    primaryContainer = IndigoSuave,
    onPrimaryContainer = IndigoPrimario,
    secondary = AccentoCian,
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = FondoApp,
    onSurfaceVariant = TextoSecundario,
    background = FondoApp,
    onBackground = TextoPrincipal,
    error = ErrorSoft,
    outline = BordeGris
)

private val EsquemaOscuro = darkColorScheme(
    primary = IndigoOscuro,
    onPrimary = FondoNoche,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = IndigoOscuro,
    secondary = AccentoCian,
    onSecondary = Color.White,
    surface = SuperficieNoche,
    onSurface = TextoNoche,
    surfaceVariant = Color(0xFF111827),
    onSurfaceVariant = TextoSecNoche,
    background = FondoNoche,
    onBackground = TextoNoche,
    error = ErrorSoft,
    outline = BordeNoche
)

@Composable
fun GestorTareasProTema(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    contenido: @Composable () -> Unit
) {
    val esquemaColores = if (temaOscuro) EsquemaOscuro else EsquemaClaro

    MaterialTheme(
        colorScheme = esquemaColores,
        typography = TipografiaApp,
        content = contenido
    )
}
