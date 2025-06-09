package com.example.scanner.data.repository

import com.example.scanner.domain.model.BarcodeData
import com.example.scanner.domain.repository.BarcodeScannerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeScannerRepositoryImpl @Inject constructor() : BarcodeScannerRepository {
    private val _barcodeSharedFlow = MutableSharedFlow<List<BarcodeData>>(replay = 0)

    override fun analyzeImageFlow(): Flow<List<BarcodeData>> = _barcodeSharedFlow.asSharedFlow()

    suspend fun onBarcodesDetected(barcodes: List<BarcodeData>) {
        _barcodeSharedFlow.emit(barcodes)
    }
}