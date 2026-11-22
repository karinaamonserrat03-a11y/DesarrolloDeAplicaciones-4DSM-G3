package com.kmro.edunote.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kmro.edunote.data.database.entity.CalificacionEntity
import kotlinx.coroutines.flow.Flow

/*
 * Objeto de acceso a datos (DAO) para gestionar los registros de la tabla calificaciones
 *
 * Proporciona consultas reactivas y operaciones de inserción para persistir y observar
 * las calificaciones registradas en la base de datos local de EduNote.
 */
@Dao
interface CalificacionDao {
    @Query("SELECT * FROM calificaciones ORDER BY fechaRegistroMillis DESC")
    fun obtenerCalificaciones(): Flow<List<CalificacionEntity>>

    /*
     * Inserta una nueva calificación o reemplaza un registro existente en caso de conflicto
     * de clave primaria
     *
     * La calificacion Entidad de calificación a guardar en la base de datos
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCalificacion(calificacion: CalificacionEntity)
}