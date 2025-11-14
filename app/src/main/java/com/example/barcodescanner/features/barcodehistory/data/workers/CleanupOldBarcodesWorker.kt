package com.example.barcodescanner.features.barcodehistory.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.barcodescanner.features.barcodehistory.domain.repository.BarcodeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Worker que se ejecuta periódicamente para limpiar códigos de barras antiguos.
 * Elimina automáticamente los códigos escaneados que tienen más de 30 días de antigüedad.
 * 
 * Configuración:
 * - Se ejecuta cada 24 horas
 * - Requiere conexión WiFi
 * - Solo se ejecuta cuando el dispositivo está cargando
 * - Usa política de reintento con backoff exponencial
 */
@HiltWorker
class CleanupOldBarcodesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: BarcodeRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Iniciando limpieza de códigos antiguos...")

            // Calcular la fecha de corte (30 días atrás)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -DAYS_TO_KEEP)
            val cutoffDate = calendar.timeInMillis

            // Contar cuántos códigos se van a eliminar
            val count = repository.countBarcodesOlderThan(cutoffDate)
            
            if (count > 0) {
                // Eliminar códigos antiguos
                repository.deleteBarcodesOlderThan(cutoffDate)
                Log.d(TAG, "Limpieza completada: $count códigos eliminados")
            } else {
                Log.d(TAG, "No hay códigos antiguos para eliminar")
            }

            Result.success()
        } catch (exception: Exception) {
            Log.e(TAG, "Error durante la limpieza de códigos: ${exception.message}", exception)
            
            // Si falla, reintentar con backoff exponencial
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        private const val TAG = "CleanupOldBarcodesWorker"
        private const val DAYS_TO_KEEP = 30
        private const val MAX_RETRIES = 3
        
        // Nombre único para este trabajo periódico
        const val WORK_NAME = "cleanup_old_barcodes_work"
        
        // Intervalo de repetición (24 horas)
        val REPEAT_INTERVAL = TimeUnit.HOURS.toMillis(24)
    }
}

