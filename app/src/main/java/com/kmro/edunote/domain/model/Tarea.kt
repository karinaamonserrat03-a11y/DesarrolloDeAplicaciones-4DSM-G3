package com.kmro.edunote.domain.model


// Modelo de dominio que representa una tarea o actividad académica dentro de EduNote
data class Tarea(
    val idTarea: Int = 0,
    val titulo: String,
    val materia: String,
    val fechaLimite: String,
    val prioridad: String = "",
    val estado: String = "",
    val notas: String = "",
    val fechaLimiteMillis: Long = 0L,
    val calificacion: Double? = null
)