package com.example.barcodescanner.data.repository

import com.example.barcodescanner.data.dao.BarcodeDao
import com.example.barcodescanner.data.entities.BarcodeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface BarcodeRepository {
    fun getAllBarcodes(): Flow<List<BarcodeEntity>>
    suspend fun insertBarcode(barcode: BarcodeEntity)
    suspend fun deleteAllBarcodes()
    suspend fun deleteBarcode(barcode: BarcodeEntity)
}

class BarcodeRepositoryImpl @Inject constructor(
    private val barcodeDao: BarcodeDao
) : BarcodeRepository {
    override fun getAllBarcodes(): Flow<List<BarcodeEntity>> = barcodeDao.getAllBarcodes()

    override suspend fun insertBarcode(barcode: BarcodeEntity) = barcodeDao.insertBarcode(barcode)

    override suspend fun deleteAllBarcodes() = barcodeDao.deleteAllBarcodes()

    override suspend fun deleteBarcode(barcode: BarcodeEntity) = barcodeDao.deleteBarcode(barcode)
}
