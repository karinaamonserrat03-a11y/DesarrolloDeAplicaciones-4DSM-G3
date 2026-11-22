package com.kmro.edunote.data.repository

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EduNoteRepositoryImpl @Inject constructor(
    private val dao: EduNoteDao,
    private val calificacionDao: CalificacionDao,
    private val refuerzoDao: RefuerzoDao
) : EduNoteRepository {

    // * Convierte el modelo  Usuario a entidad y lo guarda en la base de datos local

    override suspend fun registrarUsuario(usuario: Usuario) {
        dao.insertarUsuario(usuario.toEntity())
    }

    // * Consulta las credenciales en dao y mapea el resultado al modelo de dominio Usuario

    override suspend fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        return dao.iniciarSesion(correo, contrasena)?.toDomain()
    }
    //* Transforma el flujo reactivo de entidades de tareas en una lista de modelos Tarea

    override fun obtenerTareas(): Flow<List<Tarea>> {
        return dao.obtenerTareas().map { lista ->
            lista.map { it.toDomain() }
        }
    }
    // * Convierte el modelo Tarea a entidad y lo inserta en la tabla tareas

    override suspend fun agregarTarea(tarea: Tarea) {
        dao.insertarTarea(tarea.toEntity())
    }

    // Traduce el indicador booleano de finalización a texto Completada o Pendiente y actualiza el registro correspondiente en Room

    override suspend fun actualizarEstadoTarea(idTarea: Int, completada: Boolean) {
        val estado = if (completada) "Completada" else "Pendiente"
        dao.actualizarEstadoTarea(idTarea, estado)
    }

    // Registra la calificación de una tarea y sincroniza las tablas relacionadas

    override suspend fun calificarTarea(idTarea: Int, calificacion: Double) {
        dao.actualizarCalificacionTarea(idTarea, calificacion)

        val tareaEntity = dao.obtenerTareaPorId(idTarea)
        val materia = tareaEntity?.materia ?: "General"
        val titulo = tareaEntity?.titulo ?: "Actividad"

        // Persiste el historial individual de la evaluación en la tabla de calificaciones
        calificacionDao.insertarCalificacion(
            CalificacionEntity(
                materia = materia,
                tituloActividad = titulo,
                calificacion = calificacion
            )
        )
// Genera un registro de refuerzo automático cuando el rendimiento está en riesgo < 7.0
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

    // * Observa el perfil del usuario activo desde Room y lo proyecta al modelo de dominio Usuario
    override fun obtenerUsuarioActivo(): Flow<Usuario?> {
        return dao.obtenerUsuarioActivo().map { entity ->
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