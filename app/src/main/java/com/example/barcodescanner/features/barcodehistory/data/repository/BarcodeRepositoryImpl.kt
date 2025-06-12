package com.example.barcodescanner.features.barcodehistory.data.repository

import com.example.barcodescanner.features.barcodehistory.data.dao.BarcodeDao
import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import com.example.barcodescanner.features.barcodehistory.domain.repository.BarcodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



class BarcodeRepositoryImpl @Inject constructor(
    private val barcodeDao: BarcodeDao
) : BarcodeRepository {
    override fun getAllBarcodes(): Flow<List<BarcodeEntity>> = barcodeDao.getAllBarcodes()

    override suspend fun insertBarcode(barcode: BarcodeEntity) = barcodeDao.insertBarcode(barcode)

    override suspend fun deleteAllBarcodes() = barcodeDao.deleteAllBarcodes()

    override suspend fun deleteBarcode(barcode: BarcodeEntity) = barcodeDao.deleteBarcode(barcode)
}
