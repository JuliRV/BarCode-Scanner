package com.example.barcodescanner.features.barcodehistory.presentation.scanner

import androidx.lifecycle.ViewModel
import com.example.barcodescanner.features.barcodehistory.data.collector.BarcodeCollector
import com.example.scanner.domain.model.BarcodeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AppScannerViewModel @Inject constructor(
    private val barcodeCollector: BarcodeCollector
) : ViewModel() {

    fun collectBarcodes(barcodeFlow: StateFlow<List<BarcodeData>>) {
        barcodeCollector.collectBarcodes(barcodeFlow)
    }
}
