package com.kmro.edunote.domain.model

// * Modelo de dominio que representa el perfil de un estudiante registrado en EduNote
data class Usuario(
    val idUsuario: Int = 0,
    val correo: String,
    val contrasena: String,
    val nombreUsuario: String
)