package com.example.barcodescanner.features.barcodehistory.presentation.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barcodescanner.features.barcodehistory.data.collector.BarcodeCollector
import com.example.barcodescanner.features.barcodehistory.domain.usecases.SaveBarcodeUseCase
import com.example.scanner.domain.model.BarcodeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppScannerViewModel @Inject constructor(
    private val barcodeCollector: BarcodeCollector,
    private val saveBarcodeUseCase: SaveBarcodeUseCase
) : ViewModel() {

    fun collectBarcodes(barcodeFlow: StateFlow<List<BarcodeData>>) {
        barcodeCollector.collectBarcodes(barcodeFlow)
    }

    fun saveBarcode(barcode: BarcodeData) {
        viewModelScope.launch {
            saveBarcodeUseCase(barcode.value)
        }
    }
}
