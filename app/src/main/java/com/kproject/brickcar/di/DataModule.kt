package com.kproject.brickcar.di

import android.content.Context
import com.kproject.brickcar.data.repository.prefs.PreferenceRepository
import com.kproject.brickcar.data.repository.prefs.PreferenceRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providePreferenceRepository(
        @ApplicationContext applicationContext: Context
    ): PreferenceRepository {
        return PreferenceRepositoryImpl(applicationContext)
    }
}