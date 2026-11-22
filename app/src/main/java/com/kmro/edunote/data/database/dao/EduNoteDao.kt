package com.kmro.edunote.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kmro.edunote.data.database.entity.TareaEntity
import com.kmro.edunote.data.database.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EduNoteDao {

    // Registra un nuevo usuario en la base de datos o actualiza el registro si existe conflicto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    // Obtiene un flujo reactivo con todas las tareas almacenadas, ordenadas de la más reciente
    // a la más antigua
    @Query("SELECT * FROM tareas ORDER BY idTarea DESC")
    fun obtenerTareas(): Flow<List<TareaEntity>>

    // ESto valida el inicio de sesión
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND contrasena = :contrasena LIMIT 1")
    suspend fun iniciarSesion(correo: String, contrasena: String): UsuarioEntity?

    //Inserta una nueva tarea en la tabla TAREAS o reemplaza una existente en caso de conflicto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTarea(tarea: TareaEntity)

    // Actualiza el estado de avance de una tarea específica.
    @Query("UPDATE tareas SET estado = :nuevoEstado WHERE idTarea = :idTarea")
    suspend fun actualizarEstadoTarea(idTarea: Int, nuevoEstado: String)

    // Registra la calificación obtenida en una tarea y marca automáticamente su estado como Completada
    @Query("UPDATE tareas SET calificacion = :calificacion, estado = 'Completada' WHERE idTarea = :idTarea")
    suspend fun actualizarCalificacionTarea(idTarea: Int, calificacion: Double)

    // Consulta una tarea individual a partir de su identificador único
    @Query("SELECT * FROM tareas WHERE idTarea = :idTarea LIMIT 1")
    suspend fun obtenerTareaPorId(idTarea: Int): TareaEntity?

    // Observa en tiempo real el perfil del usuario activo almacenado
    @Query("SELECT * FROM usuarios LIMIT 1")
    fun obtenerUsuarioActivo(): Flow<UsuarioEntity?>
}