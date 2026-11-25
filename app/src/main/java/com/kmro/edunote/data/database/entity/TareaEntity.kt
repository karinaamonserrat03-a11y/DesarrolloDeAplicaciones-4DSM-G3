package com.kmro.edunote.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Entidad de Room que representa la tabla `tareas` en la base de datos local de EduNote.
 * Almacena la información de las actividades académicas, su estado de avance, fecha límite,
 * calificación obtenida y el correo del estudiante propietario del registro.
 */
@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)
    val idTarea: Int = 0,
    val correoUsuario: String = "",
    val titulo: String,
    val materia: String,
    val fechaLimite: String,
    val prioridad: String = "",
    val estado: String = "",
    val notas: String = "",
    val fechaLimiteMillis: Long = 0L,
    val calificacion: Double? = null
)