package com.example.barcodescanner.features.barcodehistory.presentation.history

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.barcodescanner.features.barcodehistory.data.workers.SyncBarcodesToServerWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Helper para gestionar la sincronización de códigos con el servidor.
 * 
 * Proporciona métodos convenientes para:
 * - Iniciar sincronización manual
 * - Observar el estado de la sincronización
 * - Obtener resultados de la sincronización
 * 
 * Ejemplo de uso en un ViewModel:
 * ```
 * class BarcodeHistoryViewModel @Inject constructor(
 *     private val context: Context
 * ) : ViewModel() {
 *     
 *     fun syncWithServer() {
 *         SyncHelper.startSync(context)
 *     }
 *     
 *     fun observeSyncStatus(): Flow<SyncStatus> {
 *         return SyncHelper.observeSyncStatus(context)
 *     }
 * }
 * ```
 */
object SyncHelper {

    /**
     * Inicia la sincronización con el servidor.
     * 
     * @param context Contexto de la aplicación
     * @return ID del trabajo programado
     */
    fun startSync(context: Context): String {
        val workManager = WorkManager.getInstance(context)
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // Solo WiFi
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncBarcodesToServerWorker>()
            .setConstraints(constraints)
            .addTag(TAG_SYNC)
            .build()

        workManager.enqueueUniqueWork(
            SyncBarcodesToServerWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE, // Reemplazar si ya hay uno en cola
            syncRequest
        )
        
        return syncRequest.id.toString()
    }

    /**
     * Observa el estado de la sincronización en tiempo real.
     * 
     * @param context Contexto de la aplicación
     * @return Flow que emite el estado actual de sincronización
     */
    fun observeSyncStatus(context: Context): Flow<SyncStatus> {
        val workManager = WorkManager.getInstance(context)
        
        return workManager.getWorkInfosForUniqueWorkFlow(
            SyncBarcodesToServerWorker.WORK_NAME
        ).map { workInfoList ->
            val workInfo = workInfoList.firstOrNull()
            
            when {
                workInfo == null -> SyncStatus.Idle
                workInfo.state == WorkInfo.State.RUNNING -> SyncStatus.Syncing
                workInfo.state == WorkInfo.State.SUCCEEDED -> {
                    val syncedCount = workInfo.outputData.getInt(
                        SyncBarcodesToServerWorker.OUTPUT_SYNCED_COUNT, 0
                    )
                    val message = workInfo.outputData.getString(
                        SyncBarcodesToServerWorker.OUTPUT_MESSAGE
                    ) ?: "Sincronización completada"
                    
                    SyncStatus.Success(syncedCount, message)
                }
                workInfo.state == WorkInfo.State.FAILED -> {
                    val message = workInfo.outputData.getString(
                        SyncBarcodesToServerWorker.OUTPUT_MESSAGE
                    ) ?: "Error en la sincronización"
                    
                    SyncStatus.Error(message)
                }
                workInfo.state == WorkInfo.State.ENQUEUED -> SyncStatus.Waiting
                else -> SyncStatus.Idle
            }
        }
    }

    /**
     * Cancela la sincronización en curso.
     * 
     * @param context Contexto de la aplicación
     */
    fun cancelSync(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(SyncBarcodesToServerWorker.WORK_NAME)
    }

    private const val TAG_SYNC = "manual_sync"
}

/**
 * Estados posibles de la sincronización
 */
sealed class SyncStatus {
    /** No hay sincronización en curso */
    object Idle : SyncStatus()
    
    /** Esperando condiciones adecuadas (ej: WiFi) */
    object Waiting : SyncStatus()
    
    /** Sincronización en progreso */
    object Syncing : SyncStatus()
    
    /** Sincronización completada exitosamente */
    data class Success(val syncedCount: Int, val message: String) : SyncStatus()
    
    /** Error durante la sincronización */
    data class Error(val message: String) : SyncStatus()
}

