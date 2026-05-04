package com.example.gestortareaspro.presentation.pantalla

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * Pantalla de Splash con animación de escala + fade.
 */
@Composable
fun PantallaSplash(
    flujoSesion: Flow<Boolean>,
    onNavegar: (sesionActiva: Boolean) -> Unit
) {
    val escala = remember { Animatable(0f) }
    val opacidad = remember { Animatable(0f) }
    var animacionTerminada by remember { mutableStateOf(false) }

    // Usar null como indicador de "aún no cargado"
    // null = DataStore no ha emitido todavía
    // true/false = valor real de sesión
    val sesionCargada by flujoSesion.collectAsState(initial = null)

    // Solo navegar cuando AMBAS condiciones se cumplen
    LaunchedEffect(animacionTerminada, sesionCargada) {
        if (animacionTerminada && sesionCargada != null) {
            onNavegar(sesionCargada!!)
        }
    }

    // Animación (independiente de DataStore)
    LaunchedEffect(Unit) {
        escala.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
        opacidad.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        delay(1000)
        animacionTerminada = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(escala.value)
                .alpha(opacidad.value)
        ) {
            // Logo circular animado
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🎯",
                        fontSize = 56.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Gestor de Hábitos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Construye mejores rutinas",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
