package com.kmro.edunote.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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
* Anteriormente se contaban con dos tablas y se implementaron otras dos
*
* Versión actual es la 4
*/
@Database(
    entities = [
        UsuarioEntity::class,
        TareaEntity::class,
        CalificacionEntity::class,
        RefuerzoEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class EduNoteDatabase : RoomDatabase() {
    abstract fun eduNoteDao(): EduNoteDao
    abstract fun calificacionDao(): CalificacionDao
    abstract fun refuerzoDao(): RefuerzoDao

    companion object {
        @Volatile
        private var INSTANCE: EduNoteDatabase? = null

        fun getDatabase(context: Context): EduNoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EduNoteDatabase::class.java,
                    "edunote_db" // AJUSTA: debe ser IGUAL al nombre que ya usas
                )
                    .fallbackToDestructiveMigration() // quítalo si ya manejas migraciones
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}