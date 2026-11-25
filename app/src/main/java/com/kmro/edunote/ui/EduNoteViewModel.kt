package com.kmro.edunote.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmro.edunote.domain.model.Tarea
import com.kmro.edunote.domain.model.Usuario
import com.kmro.edunote.domain.repository.EduNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
 * ViewModel principal de EduNote encargado de coordinar el estado de autenticación,
 * el usuario activo y las operaciones sobre las tareas académicas.*
 */
@HiltViewModel
class EduNoteViewModel @Inject constructor(
    private val repository: EduNoteRepository
) : ViewModel() {

    /* Flujo reactivo con las tareas académicas expuestas a la interfaz. */
    val tareas: StateFlow<List<Tarea>> = repository.obtenerTareas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /* Flujo reactivo con el perfil del estudiante activo. */
    val usuarioActual: StateFlow<Usuario?> = repository.obtenerUsuarioActivo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _mensajeAuth = MutableStateFlow<String?>(null)

    /* Mensaje de validación o error generado durante el registro o inicio de sesión. */
    val mensajeAuth: StateFlow<String?> = _mensajeAuth.asStateFlow()

    /*
     * Valida y registra un nuevo estudiante en el sistema.
     */
    fun registrarUsuario(
        nombreUsuario: String,
        correo: String,
        contrasena: String,
        onExito: () -> Unit
    ) {
        if (nombreUsuario.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            _mensajeAuth.value = "Completa todos los campos"
            return
        }
        viewModelScope.launch {
            val nuevoUsuario = Usuario(
                nombreUsuario = nombreUsuario.trim(),
                correo = correo.trim(),
                contrasena = contrasena.trim()
            )
            repository.registrarUsuario(nuevoUsuario)
            _mensajeAuth.value = null
            onExito()
        }
    }

    /*
     * Autentica al estudiante con su correo y contraseña.
     */
    fun iniciarSesion(
        correo: String,
        contrasena: String,
        onExito: () -> Unit
    ) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _mensajeAuth.value = "Ingresa correo y contraseña"
            return
        }
        viewModelScope.launch {
            val usuario = repository.iniciarSesion(correo.trim(), contrasena.trim())
            if (usuario != null) {
                _mensajeAuth.value = null
                onExito()
            } else {
                _mensajeAuth.value = "Correo o contraseña incorrectos"
            }
        }
    }

    /*
     * Crea y guarda una nueva tarea académica en estado `Pendiente`.
     */
    fun guardarTarea(
        titulo: String,
        materia: String,
        fechaLimite: String,
        prioridad: String,
        notas: String,
        fechaLimiteMillis: Long = 0L,
        onExito: () -> Unit
    ) {
        viewModelScope.launch {
            val nueva = Tarea(
                titulo = titulo,
                materia = materia,
                fechaLimite = fechaLimite,
                prioridad = prioridad,
                estado = "Pendiente",
                notas = notas,
                fechaLimiteMillis = fechaLimiteMillis,
                calificacion = null
            )
            repository.agregarTarea(nueva)
            onExito()
        }
    }

    /*
     * Asigna una calificación numérica a la tarea indicada.
     */
    fun calificarTarea(idTarea: Int, calificacion: Double) {
        viewModelScope.launch {
            repository.calificarTarea(idTarea, calificacion)
        }
    }

    /*
     * Limpia el mensaje actual de autenticación o validación.
     */
    fun limpiarMensaje() {
        _mensajeAuth.value = null
    }

    /*
     * Cambia el estado de completado de una tarea específica.
     */
    fun cambiarEstadoTarea(tarea: Tarea, completada: Boolean) {
        viewModelScope.launch {
            repository.actualizarEstadoTarea(tarea.idTarea, completada)
        }
    }
}