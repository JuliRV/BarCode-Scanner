package com.example.barcodescanner.core

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.barcodescanner.features.barcodehistory.data.workers.BackupBarcodesWorker
import com.example.barcodescanner.features.barcodehistory.data.workers.CleanupOldBarcodesWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidApp
class App: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        
        // Inicializar WorkManager con configuración personalizada
        WorkManager.initialize(this, workManagerConfiguration)
        
        // Programar los Workers periódicos
        schedulePeriodicWorkers()
        
        Log.d(TAG, "WorkManager inicializado y Workers programados")
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

    /**
     * Programa los Workers periódicos de la aplicación
     */
    private fun schedulePeriodicWorkers() {
        val workManager = WorkManager.getInstance(this)
        
        // 1. Programar limpieza diaria de códigos antiguos
        scheduleCleanupWork(workManager)
        
        // 2. Programar backup semanal
        scheduleBackupWork(workManager)
    }

    /**
     * Programa el Worker de limpieza de códigos antiguos
     * Se ejecuta cada 24 horas con restricciones de red y carga
     */
    private fun scheduleCleanupWork(workManager: WorkManager) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
            .build()

        val cleanupRequest = PeriodicWorkRequestBuilder<CleanupOldBarcodesWorker>(
            24, TimeUnit.HOURS,
            15, TimeUnit.MINUTES // Ventana de flexibilidad
        )
            .setConstraints(constraints)
            .addTag(TAG_CLEANUP)
            .build()

        workManager.enqueueUniquePeriodicWork(
            CleanupOldBarcodesWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Mantener el trabajo existente
            cleanupRequest
        )
        
        Log.d(TAG, "Worker de limpieza programado")
    }

    /**
     * Programa el Worker de backup semanal
     * Se ejecuta cada 7 días con restricciones de red y batería
     */
    private fun scheduleBackupWork(workManager: WorkManager) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // Solo WiFi
            .setRequiresBatteryNotLow(true)
            .build()

        val backupRequest = PeriodicWorkRequestBuilder<BackupBarcodesWorker>(
            7, TimeUnit.DAYS,
            1, TimeUnit.DAYS // Ventana de flexibilidad de 1 día
        )
            .setConstraints(constraints)
            .addTag(TAG_BACKUP)
            .build()

        workManager.enqueueUniquePeriodicWork(
            BackupBarcodesWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            backupRequest
        )
        
        Log.d(TAG, "Worker de backup programado")
    }

    companion object {
        private const val TAG = "BarcodeApp"
        private const val TAG_CLEANUP = "cleanup_work"
        private const val TAG_BACKUP = "backup_work"
    }
}