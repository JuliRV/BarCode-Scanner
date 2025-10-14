package com.example.scanner.ui.repositories

import com.example.scanner.data.repository.BarcodeScannerRepositoryImpl
import com.example.scanner.domain.model.BarcodeData
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class BarcodeScannerRepositoryImplTest {

    private lateinit var repository: BarcodeScannerRepositoryImpl

    @Before
    fun setup() {
        repository = BarcodeScannerRepositoryImpl()
    }

    @Test
    fun whenCreatedThenFlowIsEmpty() = runTest {
        var emissionReceived = false
        val job = launch {
            repository.analyzeImageFlow().collect {
                emissionReceived = true
            }
        }
        advanceTimeBy(1000) // Esperar un segundo
        assertFalse("No debería haber emisiones iniciales", emissionReceived)
        job.cancel()
    }

    @Test
    fun whenBarcodesDetectedThenEmitsToFlow() = runTest {
        val testBarcodes = listOf(
            BarcodeData("123456", 1),
            BarcodeData("789012", 2)
        )

        val channel = Channel<List<BarcodeData>>()
        val collectJob = launch {
            repository.analyzeImageFlow().take(1).collect {
                channel.send(it)
            }
        }

        // Esperar un poco para asegurar que la recolección está activa
        advanceTimeBy(100)
        repository.onBarcodesDetected(testBarcodes)

        val result = withTimeout(1000) {
            channel.receive()
        }

        assertEquals(testBarcodes, result)
        collectJob.cancel()
    }

    @Test
    fun whenMultipleEmissionsThenLastValueIsReceived() = runTest {
        val firstBarcodes = listOf(BarcodeData("123456", 1))
        val secondBarcodes = listOf(BarcodeData("789012", 2))

        var result: List<BarcodeData>? = null
        val job = launch {
            withTimeout(1000) {
                repository.analyzeImageFlow().collect {
                    result = it
                }
            }
        }

        repository.onBarcodesDetected(firstBarcodes)
        advanceTimeBy(100)
        repository.onBarcodesDetected(secondBarcodes)
        advanceTimeBy(100)

        assertEquals(secondBarcodes, result)
        job.cancel()
    }

    @Test
    fun whenEmptyListEmittedThenFlowReceivesEmptyList() = runTest {
        val testBarcodes = listOf(BarcodeData("123456", 1))

        var result: List<BarcodeData> = listOf(testBarcodes.first())
        val job = launch {
            repository.analyzeImageFlow().collect {
                result = it
            }
        }

        repository.onBarcodesDetected(testBarcodes)
        advanceTimeBy(100)
        repository.onBarcodesDetected(emptyList())
        advanceTimeBy(100)

        assertTrue(result.isEmpty())
        job.cancel()
    }
}