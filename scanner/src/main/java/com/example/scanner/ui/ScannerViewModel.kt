package com.example.scanner.ui

import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner.domain.model.BarcodeData
import com.example.scanner.domain.repository.BarcodeScannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: BarcodeScannerRepository,
    val analyzer: ImageAnalysis.Analyzer
) : ViewModel() {

    private val _barcodeFlow = MutableStateFlow<List<BarcodeData>>(emptyList())
    val barcodeFlow: StateFlow<List<BarcodeData>> = _barcodeFlow.asStateFlow()

    private val _isAdvancedMode = MutableStateFlow(false)
    val isAdvancedMode: StateFlow<Boolean> = _isAdvancedMode.asStateFlow()

    private val _selectedBarcode = MutableStateFlow<BarcodeData?>(null)
    val selectedBarcode: StateFlow<BarcodeData?> = _selectedBarcode.asStateFlow()

    init {
        viewModelScope.launch {
            repository.analyzeImageFlow()
                .distinctUntilChanged()
                .collect { barcodes ->
                    _barcodeFlow.value = barcodes
                    if (_isAdvancedMode.value && barcodes.isNotEmpty()) {
                        _selectedBarcode.value = barcodes.firstOrNull()
                    } else {
                        _selectedBarcode.value = null
                    }
                }
        }
    }

    fun toggleAdvancedMode() {
        _isAdvancedMode.value = !_isAdvancedMode.value
        if (!_isAdvancedMode.value) {
            _selectedBarcode.value = null
        }
    }

    fun clearSelectedBarcode() {
        _selectedBarcode.value = null
    }
}