package com.kmro.edunote.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
// Tabla de Base de datos de Tareas

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)
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