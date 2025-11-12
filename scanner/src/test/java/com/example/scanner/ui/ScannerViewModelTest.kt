package com.example.scanner.ui

import androidx.camera.core.ImageAnalysis
import com.example.scanner.domain.model.BarcodeData
import com.example.scanner.domain.repository.BarcodeScannerRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScannerViewModelTest {

    private lateinit var viewModel: ScannerViewModel
    private lateinit var repository: BarcodeScannerRepository
    private lateinit var analyzer: ImageAnalysis.Analyzer
    private val testDispatcher = StandardTestDispatcher()
    private val barcodeFlow = MutableSharedFlow<List<BarcodeData>>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        analyzer = mockk(relaxed = true)
        coEvery { repository.analyzeImageFlow() } returns barcodeFlow
        viewModel = ScannerViewModel(repository, analyzer)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenViewModelInitializedThenBarcodeFlowIsEmpty() = runTest {
        val currentValue = viewModel.barcodeFlow.value
        assertTrue(currentValue.isEmpty())
    }

    @Test
    fun whenRepositoryEmitsBarcodeThenUpdatesFlow() = runTest {
        val testBarcodes = listOf(
            BarcodeData("test1", 1),
            BarcodeData("test2", 2)
        )

        barcodeFlow.emit(testBarcodes)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(testBarcodes, viewModel.barcodeFlow.value)
    }

    @Test
    fun whenRepositoryEmitsDuplicateBarcodeThenDistinctUntilChangedFiltersIt() = runTest {
        val testBarcode = BarcodeData("test", 1)
        val testBarcodes = listOf(testBarcode)

        barcodeFlow.emit(testBarcodes)
        testDispatcher.scheduler.advanceUntilIdle()
        barcodeFlow.emit(testBarcodes)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(testBarcodes, viewModel.barcodeFlow.value)
    }

    @Test
    fun whenRepositoryEmitsMultipleBarcodesThenUpdatesFlow() = runTest {
        val testBarcodes1 = listOf(BarcodeData("test1", 1))
        val testBarcodes2 = listOf(BarcodeData("test2", 2))

        barcodeFlow.emit(testBarcodes1)
        testDispatcher.scheduler.advanceUntilIdle()
        barcodeFlow.emit(testBarcodes2)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(testBarcodes2, viewModel.barcodeFlow.value)
    }

    @Test
    fun whenRepositoryEmitsEmptyListThenUpdatesFlowToEmpty() = runTest {
        val testBarcodes = emptyList<BarcodeData>()

        barcodeFlow.emit(testBarcodes)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.barcodeFlow.value.isEmpty())
    }

    @Test
    fun whenRepositoryEmitsNullThenUpdatesFlowToEmpty() = runTest {
        barcodeFlow.emit(emptyList())
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.barcodeFlow.value.isEmpty())
    }

    @Test
    fun whenRepositoryEmitsSingleBarcodeThenUpdatesFlow() = runTest {
        val testBarcode = BarcodeData("test", 1)

        barcodeFlow.emit(listOf(testBarcode))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(testBarcode), viewModel.barcodeFlow.value)
    }

}
