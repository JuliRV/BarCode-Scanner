package com.example.barcodescanner.features.barcodehistory.domain.usecases

import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import com.example.barcodescanner.features.barcodehistory.domain.repository.BarcodeRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class GetBarcodesUseCase @Inject constructor(
    private val repository: BarcodeRepository
) {
    operator fun invoke(): Flow<List<BarcodeEntity>> = repository.getAllBarcodes()
}

class SaveBarcodeUseCase @Inject constructor(
    private val repository: BarcodeRepository
) {
    suspend operator fun invoke(code: String) {
        val barcodeEntity = BarcodeEntity(
            code = code,
            timestamp = Date()
        )
        repository.insertBarcode(barcodeEntity)
    }
}

class DeleteBarcodeUseCase @Inject constructor(
    private val repository: BarcodeRepository
) {
    suspend operator fun invoke(barcode: BarcodeEntity) = repository.deleteBarcode(barcode)
}

class ClearHistoryUseCase @Inject constructor(
    private val repository: BarcodeRepository
) {
    suspend operator fun invoke() = repository.deleteAllBarcodes()
}
