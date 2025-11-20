package com.example.barcodescanner.features.barcodehistory.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.barcodescanner.features.barcodehistory.data.api.BarcodeApiService
import com.example.barcodescanner.features.barcodehistory.data.api.SyncBarcodesResponse
import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import com.example.barcodescanner.features.barcodehistory.data.workers.SyncBarcodesToServerWorker
import com.example.barcodescanner.features.barcodehistory.domain.repository.BarcodeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import java.util.Date

/**
 * Tests unitarios para SyncBarcodesToServerWorker
 */
@RunWith(RobolectricTestRunner::class)
class SyncBarcodesToServerWorkerTest {

    private lateinit var context: Context
    private lateinit var repository: BarcodeRepository
    private lateinit var apiService: BarcodeApiService

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        repository = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
    }

    @Test
    fun `test sync worker sincroniza exitosamente`() = runBlocking {
        // Given - Hay códigos para sincronizar y el servidor responde OK
        val barcodes = listOf(
            BarcodeEntity(id = 1, code = "123456", timestamp = Date()),
            BarcodeEntity(id = 2, code = "789012", timestamp = Date())
        )
        coEvery { repository.getAllBarcodesSnapshot() } returns barcodes
        
        val response = SyncBarcodesResponse(
            success = true,
            message = "Sincronización exitosa",
            syncedCount = 2,
            timestamp = System.currentTimeMillis()
        )
        coEvery { apiService.syncBarcodes(any()) } returns Response.success(response)

        // When
        val worker = TestListenableWorkerBuilder<SyncBarcodesToServerWorker>(context)
            .build()

        val workerWithDeps = object : SyncBarcodesToServerWorker(
            context,
            worker.workerParams,
            repository,
            apiService
        ) {}

        val result = workerWithDeps.doWork()

        // Then
        assertEquals(ListenableWorker.Result.success(), result)
        
        // Verificar output data
        val outputData = (result as ListenableWorker.Result.Success).outputData
        assertEquals(2, outputData.getInt(SyncBarcodesToServerWorker.OUTPUT_SYNCED_COUNT, 0))
        assertNotNull(outputData.getString(SyncBarcodesToServerWorker.OUTPUT_MESSAGE))

        coVerify { repository.getAllBarcodesSnapshot() }
        coVerify { apiService.syncBarcodes(any()) }
    }

    @Test
    fun `test sync worker retorna success cuando no hay códigos`() = runBlocking {
        // Given - No hay códigos para sincronizar
        coEvery { repository.getAllBarcodesSnapshot() } returns emptyList()

        // When
        val worker = TestListenableWorkerBuilder<SyncBarcodesToServerWorker>(context)
            .build()

        val workerWithDeps = object : SyncBarcodesToServerWorker(
            context,
            worker.workerParams,
            repository,
            apiService
        ) {}

        val result = workerWithDeps.doWork()

        // Then
        assertEquals(ListenableWorker.Result.success(), result)
        
        val outputData = (result as ListenableWorker.Result.Success).outputData
        assertEquals(0, outputData.getInt(SyncBarcodesToServerWorker.OUTPUT_SYNCED_COUNT, -1))

        coVerify { repository.getAllBarcodesSnapshot() }
        coVerify(exactly = 0) { apiService.syncBarcodes(any()) }
    }

    @Test
    fun `test sync worker maneja error del servidor con modo simulado`() = runBlocking {
        // Given - El servidor no está disponible (modo demo)
        val barcodes = listOf(
            BarcodeEntity(id = 1, code = "123456", timestamp = Date())
        )
        coEvery { repository.getAllBarcodesSnapshot() } returns barcodes
        coEvery { apiService.syncBarcodes(any()) } throws Exception("Connection error")

        // When
        val worker = TestListenableWorkerBuilder<SyncBarcodesToServerWorker>(context)
            .build()

        val workerWithDeps = object : SyncBarcodesToServerWorker(
            context,
            worker.workerParams,
            repository,
            apiService
        ) {}

        val result = workerWithDeps.doWork()

        // Then - Debería retornar success en modo demo
        assertEquals(ListenableWorker.Result.success(), result)
        
        val outputData = (result as ListenableWorker.Result.Success).outputData
        val message = outputData.getString(SyncBarcodesToServerWorker.OUTPUT_MESSAGE)
        assertNotNull(message)
        assertTrue("El mensaje debe indicar modo simulado", 
            message?.contains("simulada") == true || message?.contains("mock") == true)
    }

    @Test
    fun `test sync worker reintenta en caso de error del repositorio`() = runBlocking {
        // Given - Error al obtener datos del repositorio
        coEvery { repository.getAllBarcodesSnapshot() } throws Exception("Database error")

        // When
        val worker = TestListenableWorkerBuilder<SyncBarcodesToServerWorker>(context)
            .setRunAttemptCount(0)
            .build()

        val workerWithDeps = object : SyncBarcodesToServerWorker(
            context,
            worker.workerParams,
            repository,
            apiService
        ) {}

        val result = workerWithDeps.doWork()

        // Then
        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun `test sync worker falla después de máximo de reintentos`() = runBlocking {
        // Given - Error persistente
        coEvery { repository.getAllBarcodesSnapshot() } throws Exception("Persistent error")

        // When
        val worker = TestListenableWorkerBuilder<SyncBarcodesToServerWorker>(context)
            .setRunAttemptCount(3)
            .build()

        val workerWithDeps = object : SyncBarcodesToServerWorker(
            context,
            worker.workerParams,
            repository,
            apiService
        ) {}

        val result = workerWithDeps.doWork()

        // Then
        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun `test sync worker maneja respuesta de error del servidor`() = runBlocking {
        // Given - El servidor responde con error
        val barcodes = listOf(
            BarcodeEntity(id = 1, code = "123456", timestamp = Date())
        )
        coEvery { repository.getAllBarcodesSnapshot() } returns barcodes
        coEvery { apiService.syncBarcodes(any()) } returns Response.error(500, mockk(relaxed = true))

        // When
        val worker = TestListenableWorkerBuilder<SyncBarcodesToServerWorker>(context)
            .setRunAttemptCount(0)
            .build()

        val workerWithDeps = object : SyncBarcodesToServerWorker(
            context,
            worker.workerParams,
            repository,
            apiService
        ) {}

        val result = workerWithDeps.doWork()

        // Then - Debe reintentar
        assertEquals(ListenableWorker.Result.retry(), result)
    }
}

