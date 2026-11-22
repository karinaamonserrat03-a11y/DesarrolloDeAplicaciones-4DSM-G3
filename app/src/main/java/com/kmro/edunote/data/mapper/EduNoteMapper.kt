package com.kmro.edunote.data.mapper

import com.kmro.edunote.data.database.entity.TareaEntity
import com.kmro.edunote.data.database.entity.UsuarioEntity
import com.kmro.edunote.domain.model.Tarea
import com.kmro.edunote.domain.model.Usuario

/*
 * Convierte una entidad de base de datos UsuarioEntity a su modelo de dominio Usuario
 *
 * Permite trasladar los datos persistidos en Room hacia las capas de negocio y presentación
 * sin acoplarlas directamente a la estructura de la tabla
 */
fun UsuarioEntity.toDomain(): Usuario = Usuario(
    idUsuario = idUsuario,
    nombreUsuario = nombreUsuario,
    correo = correo,
    contrasena = contrasena
)

/*
 * Convierte un modelo de dominio Usuario a su entidad de persistencia UsuarioEntity
 * Prepara la información del estudiante para ser insertada o actualizada en la base de datos local
 */
fun Usuario.toEntity(): UsuarioEntity = UsuarioEntity(
    idUsuario = idUsuario,
    nombreUsuario = nombreUsuario,
    correo = correo,
    contrasena = contrasena
)


/*
 * Convierte una entidad de base de datos TareaEntity a su modelo de dominio Tarea
 *
 * Mapea los atributos de la actividad almacenada en Room para su uso en la lógica de negocio
 * y en las pantallas de la aplicación
 */
fun TareaEntity.toDomain(): Tarea = Tarea(
    idTarea = idTarea,
    titulo = titulo,
    materia = materia,
    fechaLimite = fechaLimite,
    prioridad = prioridad,
    estado = estado,
    notas = notas,
    fechaLimiteMillis = fechaLimiteMillis,
    calificacion = calificacion
)

/*
 * Convierte un modelo de dominio Tarea a su entidad de persistencia TareaEntity
 * Adapta los datos de la tarea para almacenarlos o actualizarlos en la tabla tareas de Room
 */
fun Tarea.toEntity(): TareaEntity = TareaEntity(
    idTarea = idTarea,
    titulo = titulo,
    materia = materia,
    fechaLimite = fechaLimite,
    prioridad = prioridad,
    estado = estado,
    notas = notas,
    fechaLimiteMillis = fechaLimiteMillis,
    calificacion = calificacion
)