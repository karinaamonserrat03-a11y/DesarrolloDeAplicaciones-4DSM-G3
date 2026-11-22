package com.kmro.edunote.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de Base de datos de Calificaciones

@Entity(tableName = "calificaciones")
data class CalificacionEntity(
    @PrimaryKey(autoGenerate = true)
    val idCalificacion: Int = 0,
    val materia: String,
    val tituloActividad: String = "",
    val calificacion: Double,
    val fechaRegistroMillis: Long = System.currentTimeMillis()
)