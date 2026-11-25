package com.kmro.edunote.util

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestor de sesión local basado en `SharedPreferences` y `StateFlow` para EduNote.
 *
 * Persiste el correo del estudiante con sesión activa, expone su estado en tiempo real
 * y registra la aceptación de los Términos de Servicio y la Política de Privacidad por cuenta.
 */
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("edunote_session", Context.MODE_PRIVATE)

    private val _correoActivoFlow = MutableStateFlow(obtenerCorreoActivo())

    /** Flujo reactivo que emite el correo de la sesión activa o `null` si no hay sesión. */
    val correoActivoFlow: StateFlow<String?> = _correoActivoFlow.asStateFlow()

    /**
     * Guarda el correo electrónico del usuario que ha iniciado sesión y notifica a los observadores.
     *
     * @param correo Correo electrónico del estudiante activo.
     */
    fun guardarSesion(correo: String) {
        val correoLimpio = correo.trim()
        prefs.edit().putString("correo_activo", correoLimpio).apply()
        _correoActivoFlow.value = correoLimpio
    }

    /**
     * Obtiene el correo electrónico almacenado para la sesión activa.
     *
     * @return El correo activo si existe, o `null` si no hay sesión iniciada.
     */
    fun obtenerCorreoActivo(): String? {
        return prefs.getString("correo_activo", null)
    }

    /**
     * Registra si el estudiante ha aceptado los Términos de Servicio y la Política de Privacidad.
     *
     * @param correo Correo electrónico del estudiante que otorga el consentimiento.
     * @param aceptado `true` si aceptó los términos, `false` en caso contrario.
     */
    fun guardarAceptacionTerminos(correo: String, aceptado: Boolean) {
        val clave = "terminos_aceptados_${correo.trim().lowercase()}"
        prefs.edit().putBoolean(clave, aceptado).apply()
    }

    /**
     * Verifica si la cuenta indicada ya aceptó los Términos y Política de Privacidad.
     *
     * @param correo Correo electrónico del estudiante a consultar.
     * @return `true` si ya existe registro de aceptación; `false` si aún no acepta.
     */
    fun haAceptadoTerminos(correo: String): Boolean {
        val clave = "terminos_aceptados_${correo.trim().lowercase()}"
        return prefs.getBoolean(clave, false)
    }

    /**
     * Elimina el correo de la sesión activa al cerrar sesión y actualiza el flujo reactivo.
     */
    fun cerrarSesion() {
        prefs.edit().remove("correo_activo").apply()
        _correoActivoFlow.value = null
    }
}