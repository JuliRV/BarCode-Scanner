package com.example.barcodescanner.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barcodescanner.data.entities.BarcodeEntity
import com.example.barcodescanner.domain.usecases.ClearHistoryUseCase
import com.example.barcodescanner.domain.usecases.DeleteBarcodeUseCase
import com.example.barcodescanner.domain.usecases.GetBarcodesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarcodeHistoryViewModel @Inject constructor(
    getBarcodesUseCase: GetBarcodesUseCase,
    private val deleteBarcodeUseCase: DeleteBarcodeUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    val barcodes: StateFlow<List<BarcodeEntity>> = getBarcodesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteBarcode(barcode: BarcodeEntity) {
        viewModelScope.launch {
            deleteBarcodeUseCase(barcode)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            clearHistoryUseCase()
        }
    }
}
