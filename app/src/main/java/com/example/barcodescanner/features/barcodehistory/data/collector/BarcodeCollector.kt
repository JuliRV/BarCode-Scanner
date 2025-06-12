package com.example.barcodescanner.features.barcodehistory.data.collector

import com.example.barcodescanner.features.barcodehistory.domain.usecases.SaveBarcodeUseCase
import com.example.scanner.domain.model.BarcodeData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeCollector @Inject constructor(
    private val saveBarcodeUseCase: SaveBarcodeUseCase
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun collectBarcodes(barcodeFlow: Flow<List<BarcodeData>>) {
        barcodeFlow
            .onEach { barcodes ->
                barcodes.forEach { barcode ->
                    barcode.value?.let { code ->
                        saveBarcodeUseCase(code)
                    }
                }
            }
            .launchIn(scope)
    }
}
