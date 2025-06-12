package com.example.barcodescanner.features.barcodehistory.domain.repository

import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import kotlinx.coroutines.flow.Flow

interface BarcodeRepository {
    fun getAllBarcodes(): Flow<List<BarcodeEntity>>
    suspend fun insertBarcode(barcode: BarcodeEntity)
    suspend fun deleteAllBarcodes()
    suspend fun deleteBarcode(barcode: BarcodeEntity)
}