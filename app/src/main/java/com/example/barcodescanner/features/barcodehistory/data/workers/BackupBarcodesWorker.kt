package com.example.barcodescanner.features.barcodehistory.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.barcodescanner.features.barcodehistory.domain.repository.BarcodeRepository
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Worker que se ejecuta periódicamente para hacer backup del historial de códigos.
 * Exporta todos los códigos escaneados a un archivo JSON en el almacenamiento interno.
 * 
 * Configuración:
 * - Se ejecuta semanalmente
 * - Requiere conexión WiFi
 * - Solo se ejecuta cuando la batería está por encima del 15%
 * - Usa política de reintento con backoff exponencial
 */
@HiltWorker
class BackupBarcodesWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: BarcodeRepository
) : CoroutineWorker(appContext, workerParams) {

    private val gson = Gson()

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Iniciando backup de códigos...")

            // Obtener todos los códigos
            val barcodes = repository.getAllBarcodesSnapshot()
            
            if (barcodes.isEmpty()) {
                Log.d(TAG, "No hay códigos para hacer backup")
                return Result.success()
            }

            // Crear el directorio de backups si no existe
            val backupDir = File(appContext.filesDir, BACKUP_DIR)
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            // Crear el archivo de backup con timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
            val backupFile = File(backupDir, "backup_$timestamp.json")

            // Convertir a JSON y guardar
            val json = gson.toJson(BackupData(
                timestamp = System.currentTimeMillis(),
                count = barcodes.size,
                barcodes = barcodes.map { 
                    BarcodeBackup(
                        id = it.id,
                        code = it.code,
                        timestamp = it.timestamp.time
                    )
                }
            ))

            backupFile.writeText(json)

            // Limpiar backups antiguos (mantener solo los últimos 5)
            cleanupOldBackups(backupDir)

            Log.d(TAG, "Backup completado: ${barcodes.size} códigos guardados en ${backupFile.name}")
            
            Result.success()
        } catch (exception: Exception) {
            Log.e(TAG, "Error durante el backup: ${exception.message}", exception)
            
            // Si falla, reintentar con backoff exponencial
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    /**
     * Limpia backups antiguos, manteniendo solo los últimos MAX_BACKUPS_TO_KEEP
     */
    private fun cleanupOldBackups(backupDir: File) {
        try {
            val backupFiles = backupDir.listFiles()
                ?.filter { it.name.startsWith("backup_") && it.name.endsWith(".json") }
                ?.sortedByDescending { it.lastModified() }
                ?: return

            // Si hay más backups de los permitidos, eliminar los más antiguos
            if (backupFiles.size > MAX_BACKUPS_TO_KEEP) {
                backupFiles.drop(MAX_BACKUPS_TO_KEEP).forEach { file ->
                    if (file.delete()) {
                        Log.d(TAG, "Backup antiguo eliminado: ${file.name}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al limpiar backups antiguos: ${e.message}", e)
        }
    }

    /**
     * Clase de datos para el backup JSON
     */
    data class BackupData(
        val timestamp: Long,
        val count: Int,
        val barcodes: List<BarcodeBackup>
    )

    data class BarcodeBackup(
        val id: Int,
        val code: String,
        val timestamp: Long
    )

    companion object {
        private const val TAG = "BackupBarcodesWorker"
        private const val BACKUP_DIR = "barcode_backups"
        private const val MAX_BACKUPS_TO_KEEP = 5
        private const val MAX_RETRIES = 3
        
        // Nombre único para este trabajo periódico
        const val WORK_NAME = "backup_barcodes_work"
        
        // Intervalo de repetición (7 días)
        val REPEAT_INTERVAL = TimeUnit.DAYS.toMillis(7)
    }
}

