package com.kmro.edunote.data.di

import com.kmro.edunote.data.repository.EduNoteRepositoryImpl
import com.kmro.edunote.domain.repository.EduNoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEduNoteRepository(
        impl: EduNoteRepositoryImpl
    ): EduNoteRepository
}