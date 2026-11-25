package com.kmro.edunote.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kmro.edunote.data.database.entity.TareaEntity
import com.kmro.edunote.data.database.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

/*
 * Objeto de acceso a datos (DAO) principal de EduNote para operaciones de usuarios y tareas.
 */
@Dao
interface EduNoteDao {

    /*
     * Registra un nuevo usuario o reemplaza uno existente en caso de conflicto.
     *
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity)
    /*
     * Obtiene un flujo reactivo con todas las tareas almacenadas, ordenadas de la más reciente
     * a la más antigua.
     */
    @Query("SELECT * FROM tareas ORDER BY idTarea DESC")
    fun obtenerTareas(): Flow<List<TareaEntity>>

    /*
     * Obtiene un flujo reactivo con las tareas pertenecientes a un usuario específico
     */
    @Query("SELECT * FROM tareas WHERE correoUsuario = :correo ORDER BY idTarea DESC")
    fun obtenerTareasPorCorreo(correo: String): Flow<List<TareaEntity>>

    /*
     * Valida las credenciales de inicio de sesión contra la tabla usuarios
     */
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND contrasena = :contrasena LIMIT 1")
    suspend fun iniciarSesion(correo: String, contrasena: String): UsuarioEntity?

    //Obtiene un usuario a partir de su correo electrónico

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerUsuarioPorCorreo(correo: String): UsuarioEntity?

    //Inserta una nueva tarea en la tabla tareas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTarea(tarea: TareaEntity)

    //Actualiza el estado de avance de una tarea específica.

    @Query("UPDATE tareas SET estado = :nuevoEstado WHERE idTarea = :idTarea")
    suspend fun actualizarEstadoTarea(idTarea: Int, nuevoEstado: String)

    // Registra la calificación de una tarea y marca automáticamente su estado como `Completada`.

    @Query("UPDATE tareas SET calificacion = :calificacion, estado = 'Completada' WHERE idTarea = :idTarea")
    suspend fun actualizarCalificacionTarea(idTarea: Int, calificacion: Double)

    //Consulta una tarea individual a partir de su identificador único.

    @Query("SELECT * FROM tareas WHERE idTarea = :idTarea LIMIT 1")
    suspend fun obtenerTareaPorId(idTarea: Int): TareaEntity?

    // Observa el primer registro de usuario almacenado en la tabla usuarios

    @Query("SELECT * FROM usuarios LIMIT 1")
    fun obtenerUsuarioActivo(): Flow<UsuarioEntity?>


    // Observa en tiempo real el perfil del usuario que coincide con el correo activo.
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    fun observarUsuarioPorCorreo(correo: String): Flow<UsuarioEntity?>
}