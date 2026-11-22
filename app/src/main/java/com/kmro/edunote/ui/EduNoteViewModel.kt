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

@HiltViewModel
class EduNoteViewModel @Inject constructor(
    private val repository: EduNoteRepository
) : ViewModel() {

    val tareas: StateFlow<List<Tarea>> = repository.obtenerTareas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val usuarioActual: StateFlow<Usuario?> = repository.obtenerUsuarioActivo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _mensajeAuth = MutableStateFlow<String?>(null)
    val mensajeAuth: StateFlow<String?> = _mensajeAuth.asStateFlow()

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

    fun calificarTarea(idTarea: Int, calificacion: Double) {
        viewModelScope.launch {
            repository.calificarTarea(idTarea, calificacion)
        }
    }

    fun limpiarMensaje() {
        _mensajeAuth.value = null
    }

    fun cambiarEstadoTarea(tarea: Tarea, completada: Boolean) {
        viewModelScope.launch {
            repository.actualizarEstadoTarea(tarea.idTarea, completada)
        }
    }
}
