package com.example.scanner.domain.repository

import com.example.scanner.domain.model.BarcodeData
import kotlinx.coroutines.flow.Flow

interface BarcodeScannerRepository {
    fun analyzeImageFlow(): Flow<List<BarcodeData>>
}
