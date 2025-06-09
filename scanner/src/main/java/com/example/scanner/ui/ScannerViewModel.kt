package com.example.scanner.ui

import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner.domain.model.BarcodeData
import com.example.scanner.domain.repository.BarcodeScannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: BarcodeScannerRepository,
    val analyzer: ImageAnalysis.Analyzer
) : ViewModel() {

    private val _barcodeFlow = MutableStateFlow<List<BarcodeData>>(emptyList())
    val barcodeFlow: StateFlow<List<BarcodeData>> = _barcodeFlow.asStateFlow()

    init {
        viewModelScope.launch {
            repository.analyzeImageFlow()
                .distinctUntilChanged()
                .collect { barcodes ->
                    _barcodeFlow.value = barcodes
                }
        }
    }
}