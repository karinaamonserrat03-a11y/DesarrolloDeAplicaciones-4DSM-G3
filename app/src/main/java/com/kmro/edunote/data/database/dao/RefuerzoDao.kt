package com.kmro.edunote.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kmro.edunote.data.database.entity.RefuerzoEntity
import kotlinx.coroutines.flow.Flow

// Permite consultar de forma reactiva los planes o materiales de refuerzo académico
@Dao
interface RefuerzoDao {
    // Obtiene un flujo de todos los refuerzos académicos registrados
    @Query("SELECT * FROM refuerzos ORDER BY idRefuerzo DESC")
    fun obtenerRefuerzos(): Flow<List<RefuerzoEntity>>

    // Inserta un nuevo registro de refuerzo académico o reemplaza uno existente
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRefuerzo(refuerzo: RefuerzoEntity)
}