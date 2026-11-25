package com.kmro.edunote.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/*
 * Migración de la base de datos de la versión 3 a la versión 4
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Añade la columna de aislamiento por correo manteniendo compatibilidad con filas anteriores.
        db.execSQL(
            "ALTER TABLE tareas ADD COLUMN correoUsuario TEXT NOT NULL DEFAULT ''"
        )
    }
}