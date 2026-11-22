package com.kmro.edunote.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de Base de datos de Refuerzos

@Entity(tableName = "refuerzos")
data class RefuerzoEntity(
    @PrimaryKey(autoGenerate = true)
    val idRefuerzo: Int = 0,
    val materia: String,
    val calificacion: Double,
    val resumen: String,
    val ejerciciosResueltos: String,
    val recursoVideo: String = "",
    val completado: Boolean = false
)