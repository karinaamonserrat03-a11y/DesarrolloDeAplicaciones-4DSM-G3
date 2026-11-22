package com.kmro.edunote.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de Base de datos de Usuarios

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val idUsuario: Int = 0,
    val nombreUsuario: String,
    val correo: String,
    val contrasena: String
)