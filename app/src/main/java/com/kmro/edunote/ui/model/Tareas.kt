package com.kmro.edunote.ui.model

/**
 * Modelo de datos de interfaz utilizado para representar un elemento de entrega
 * dentro de la pantalla de inicio de EduNote
 * Agrupa la información visual mínima requerida por los componentes de lista para mostrar
 * el título de la actividad, su detalle resumido y su nivel de prioridad.

 */
data class Tarea(
    val titulo: String,
    val detalle: String,
    val prioridad: String
)