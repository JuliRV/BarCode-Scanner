package com.example.barcodescanner.features.barcodehistory.di

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para proporcionar WorkManager y su configuración.
 * 
 * Este módulo configura WorkManager con integración de Hilt, permitiendo
 * la inyección de dependencias en los Workers.
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {

    /**
     * Proporciona la instancia de WorkManager
     */
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    /**
     * Proporciona la configuración personalizada de WorkManager
     * 
     * Nota: Esta configuración se usará en App.kt para inicializar WorkManager
     * con HiltWorkerFactory
     */
    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(
        workerFactory: androidx.work.WorkerFactory
    ): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
    }
}

