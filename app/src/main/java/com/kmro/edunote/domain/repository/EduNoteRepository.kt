package com.kmro.edunote.domain.repository

import com.kmro.edunote.domain.model.Tarea
import com.kmro.edunote.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

// Define las operaciones disponibles para gestionar usuarios y tareas sin exponer
// los detalles de implementación de la fuente de datos
interface EduNoteRepository {

    // * Registra un nuevo estudiante en el sistema
    suspend fun registrarUsuario(usuario: Usuario)

    // * Valida las credenciales de acceso de un estudiante
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario?

    // * Obtiene un flujo del listado completo de tareas almacenadas
    fun obtenerTareas(): Flow<List<Tarea>>

    // Alamcena una nueva Tarea
    suspend fun agregarTarea(tarea: Tarea)

    // * Actualiza el estado de finalización de una tarea específica
    suspend fun actualizarEstadoTarea(idTarea: Int, completada: Boolean)

    // * Asigna una calificación numérica a una actividad y actualiza su estado
    suspend fun calificarTarea(idTarea: Int, calificacion: Double)

    // * Observa en tiempo real el perfil del estudiante con sesión activa
    fun obtenerUsuarioActivo(): Flow<Usuario?>

}