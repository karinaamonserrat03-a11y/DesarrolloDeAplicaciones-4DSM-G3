package com.kmro.edunote.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kmro.edunote.data.database.dao.CalificacionDao
import com.kmro.edunote.data.database.dao.EduNoteDao
import com.kmro.edunote.data.database.dao.RefuerzoDao
import com.kmro.edunote.data.database.entity.CalificacionEntity
import com.kmro.edunote.data.database.entity.RefuerzoEntity
import com.kmro.edunote.data.database.entity.TareaEntity
import com.kmro.edunote.data.database.entity.UsuarioEntity


/*
* Base de datos local de EduNote implementada con Room
*
* Centraliza las tablas de usuarios, tareas, calificaciones y refuerzos académicos,
* exponiendo los DAOs correspondientes para las operaciones de persistencia.
* Anteriormente se contaban con dos tablas y se implenetaron otras dos
*
* Versión actual es la 3
*/
@Database(
    entities = [
        UsuarioEntity::class,
        TareaEntity::class,
        CalificacionEntity::class,
        RefuerzoEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class EduNoteDatabase : RoomDatabase() {
    abstract fun eduNoteDao(): EduNoteDao
    abstract fun calificacionDao(): CalificacionDao
    abstract fun refuerzoDao(): RefuerzoDao
}