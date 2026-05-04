package com.example.gestortareaspro.domain.model

/**
 * Modelo de dominio puro. No depende de Room ni de
 * ninguna librería externa. Facilita el testing y
 * la portabilidad entre capas.
 */
data class Tarea(
    val id: Int = 0,
    val titulo: String,
    val completada: Boolean = false
)
