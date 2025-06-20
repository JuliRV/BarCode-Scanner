package com.example.barcodescanner.features.barcodehistory.viewmodels

import com.example.scanner.domain.model.BarcodeData
import com.example.barcodescanner.features.barcodehistory.data.collector.BarcodeCollector
import com.example.barcodescanner.features.barcodehistory.presentation.scanner.AppScannerViewModel
import io.mockk.MockKAnnotations
import io.mockk.verify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Test

class AppScannerViewModelTest {

    @MockK(relaxed = true)
    private lateinit var barcodeCollector: BarcodeCollector

    private lateinit var viewModel: AppScannerViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = AppScannerViewModel(barcodeCollector)
    }

    @Test
    fun testCollectBarcodes() {
        // Se crea un StateFlow simulando una lista vacia
        val testFlow: StateFlow<List<BarcodeData>> = MutableStateFlow(emptyList())
        viewModel.collectBarcodes(testFlow)
        verify { barcodeCollector.collectBarcodes(testFlow) }
    }

    @Test
    fun testCollectBarcodesWithData() {
        // Se crea un StateFlow simulando una lista con datos
        val testData = listOf(BarcodeData("1234567890", 1))
        val testFlow: StateFlow<List<BarcodeData>> = MutableStateFlow(testData)
        viewModel.collectBarcodes(testFlow)
        verify { barcodeCollector.collectBarcodes(testFlow) }
    }

    @Test
    fun testCollectBarcodesWithEmptyData() {
        // Se crea un StateFlow simulando una lista vacia
        val testFlow: StateFlow<List<BarcodeData>> = MutableStateFlow(emptyList())
        viewModel.collectBarcodes(testFlow)
        verify { barcodeCollector.collectBarcodes(testFlow) }
    }

}