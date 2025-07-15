package com.example.scanner.data.analyzer

import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.scanner.data.repository.BarcodeScannerRepositoryImpl
import com.example.scanner.domain.model.BarcodeData
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeAnalyzer @Inject constructor(
    private val repository: BarcodeScannerRepositoryImpl
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()
    private val scope = CoroutineScope(Dispatchers.Main)

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val rotation = imageProxy.imageInfo.rotationDegrees
            val image = InputImage.fromMediaImage(mediaImage, rotation)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val barcodeDatas = barcodes.map { barcode ->
                        val boundingBox = barcode.boundingBox?.let { originalBox ->
                            when (rotation) {
                                0 -> originalBox
                                90 -> Rect(
                                    originalBox.left,
                                    originalBox.top,
                                    originalBox.right,
                                    originalBox.bottom
                                )
                                180 -> Rect(
                                    mediaImage.width - originalBox.right,
                                    mediaImage.height - originalBox.bottom,
                                    mediaImage.width - originalBox.left,
                                    mediaImage.height - originalBox.top
                                )
                                270 -> Rect(
                                    mediaImage.height - originalBox.bottom,
                                    originalBox.left,
                                    mediaImage.height - originalBox.top,
                                    originalBox.right
                                )
                                else -> originalBox
                            }
                        }

                        BarcodeData(
                            value = barcode.rawValue ?: "",
                            format = barcode.format,
                            boundingBox = boundingBox
                        )
                    }
                    scope.launch {
                        repository.onBarcodesDetected(barcodeDatas)
                    }
                }
                .addOnFailureListener {
                    scope.launch {
                        repository.onBarcodesDetected(emptyList())
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
