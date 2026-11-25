package com.kmro.edunote.data.repository

import android.content.Context
import com.kmro.edunote.data.database.dao.CalificacionDao
import com.kmro.edunote.data.database.dao.EduNoteDao
import com.kmro.edunote.data.database.dao.RefuerzoDao
import com.kmro.edunote.data.database.entity.CalificacionEntity
import com.kmro.edunote.data.database.entity.RefuerzoEntity
import com.kmro.edunote.data.mapper.toDomain
import com.kmro.edunote.data.mapper.toEntity
import com.kmro.edunote.domain.model.Tarea
import com.kmro.edunote.domain.model.Usuario
import com.kmro.edunote.domain.repository.EduNoteRepository
import com.kmro.edunote.util.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta de [EduNoteRepository] respaldada por Room Database y [SessionManager].
 *
 * Coordina el registro, la autenticación y el aislamiento por cuenta de usuario tanto para el
 * perfil activo como para las tareas académicas, calificaciones y refuerzos.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class EduNoteRepositoryImpl @Inject constructor(
    private val dao: EduNoteDao,
    private val calificacionDao: CalificacionDao,
    private val refuerzoDao: RefuerzoDao,
    @ApplicationContext context: Context
) : EduNoteRepository {

    private val sessionManager = SessionManager(context)

    /**
     * Convierte el modelo [Usuario] a entidad, lo persiste en Room y establece su correo
     * como sesión activa.
     *
     * @param usuario Datos del estudiante a registrar.
     */
    override suspend fun registrarUsuario(usuario: Usuario) {
        dao.insertarUsuario(usuario.toEntity())
        sessionManager.guardarSesion(usuario.correo)
    }

    /**
     * Consulta las credenciales en el DAO, guarda la sesión si son válidas y mapea el
     * resultado al modelo de dominio [Usuario].
     *
     * @param correo Correo electrónico del usuario.
     * @param contrasena Contraseña asociada a la cuenta.
     * @return El [Usuario] autenticado o `null` si las credenciales no coinciden.
     */
    override suspend fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        val usuario = dao.iniciarSesion(correo, contrasena)?.toDomain()
        if (usuario != null) {
            sessionManager.guardarSesion(usuario.correo)
        }
        return usuario
    }

    /**
     * Observa de forma reactiva las tareas pertenecientes únicamente al correo de la sesión activa
     * y las transforma en modelos de dominio [Tarea].
     */
    override fun obtenerTareas(): Flow<List<Tarea>> {
        return sessionManager.correoActivoFlow.flatMapLatest { correo ->
            if (correo.isNullOrBlank()) {
                flowOf(emptyList())
            } else {
                dao.obtenerTareasPorCorreo(correo).map { lista ->
                    lista.map { it.toDomain() }
                }
            }
        }
    }

    /**
     * Convierte el modelo [Tarea] a entidad, le asigna el correo de la sesión activa y lo
     * inserta en la tabla `tareas`.
     *
     * @param tarea Tarea académica a guardar.
     */
    override suspend fun agregarTarea(tarea: Tarea) {
        val correoActivo = sessionManager.obtenerCorreoActivo().orEmpty()
        dao.insertarTarea(tarea.toEntity().copy(correoUsuario = correoActivo))
    }

    /**
     * Traduce el indicador booleano de finalización a texto (`Completada` o `Pendiente`) y
     * actualiza el registro correspondiente en Room.
     *
     * @param idTarea Identificador único de la tarea.
     * @param completada `true` para marcar como completada, `false` para pendiente.
     */
    override suspend fun actualizarEstadoTarea(idTarea: Int, completada: Boolean) {
        val estado = if (completada) "Completada" else "Pendiente"
        dao.actualizarEstadoTarea(idTarea, estado)
    }

    /**
     * Registra la calificación de una tarea y sincroniza el historial de calificaciones y el
     * refuerzo automático cuando el puntaje es menor a `7.0`.
     *
     * @param idTarea Identificador único de la tarea evaluada.
     * @param calificacion Puntaje obtenido en la escala de `0.0` a `10.0`.
     */
    override suspend fun calificarTarea(idTarea: Int, calificacion: Double) {
        dao.actualizarCalificacionTarea(idTarea, calificacion)

        val tareaEntity = dao.obtenerTareaPorId(idTarea)
        val materia = tareaEntity?.materia ?: "General"
        val titulo = tareaEntity?.titulo ?: "Actividad"

        // Persiste el historial individual de la evaluación en la tabla de calificaciones.
        calificacionDao.insertarCalificacion(
            CalificacionEntity(
                materia = materia,
                tituloActividad = titulo,
                calificacion = calificacion
            )
        )

        // Genera un registro de refuerzo automático cuando el rendimiento está en riesgo (< 7.0).
        if (calificacion < 7.0) {
            refuerzoDao.insertarRefuerzo(
                RefuerzoEntity(
                    materia = materia,
                    calificacion = calificacion,
                    resumen = "Repaso de conceptos clave de $materia a partir de \"$titulo\".",
                    ejerciciosResueltos = "Resolver 3 ejercicios guiados de $materia y revisar errores de \"$titulo\".",
                    recursoVideo = "Video de repaso: fundamentos de $materia",
                    completado = false
                )
            )
        }
    }

    /**
     * Observa en tiempo real el perfil del usuario correspondiente al correo activo y lo
     * proyecta al modelo de dominio [Usuario].
     */
    override fun obtenerUsuarioActivo(): Flow<Usuario?> {
        return sessionManager.correoActivoFlow.flatMapLatest { correo ->
            if (correo.isNullOrBlank()) {
                flowOf(null)
            } else {
                dao.observarUsuarioPorCorreo(correo).map { entity ->
                    entity?.let {
                        Usuario(
                            idUsuario = it.idUsuario,
                            nombreUsuario = it.nombreUsuario,
                            correo = it.correo,
                            contrasena = it.contrasena
                        )
                    }
                }
            }
        }
    }
}