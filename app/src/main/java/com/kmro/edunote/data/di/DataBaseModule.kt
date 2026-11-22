package com.kmro.edunote.data.di

import android.content.Context
import androidx.room.Room
import com.kmro.edunote.data.database.EduNoteDatabase
import com.kmro.edunote.data.database.dao.CalificacionDao
import com.kmro.edunote.data.database.dao.EduNoteDao
import com.kmro.edunote.data.database.dao.RefuerzoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EduNoteDatabase {
        return Room.databaseBuilder(
            context,
            EduNoteDatabase::class.java,
            "edunote_db"
        ).build()
    }

    @Provides
    fun provideEduNoteDao(database: EduNoteDatabase): EduNoteDao {
        return database.eduNoteDao()
    }
    @Provides
    fun provideCalificacionDao(db: EduNoteDatabase): CalificacionDao = db.calificacionDao()

    @Provides
    fun provideRefuerzoDao(db: EduNoteDatabase): RefuerzoDao = db.refuerzoDao()
}