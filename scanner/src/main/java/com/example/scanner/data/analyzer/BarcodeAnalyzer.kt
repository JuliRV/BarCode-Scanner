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
import kotlin.math.abs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeAnalyzer @Inject constructor(
    private val repository: BarcodeScannerRepositoryImpl
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()
    private val scope = CoroutineScope(Dispatchers.Main)

    private var lastUIUpdateTime = 0L
    private val UI_UPDATE_DELAY_MS = 80L // Reducido para mayor suavidad
    private var lastBoundingBox: Rect? = null
    private var smoothBoundingBox: Rect? = null
    private val MIN_POSITION_CHANGE = 12 // Reducido para más sensibilidad
    private val SMOOTHING_FACTOR = 0.3f // Factor de suavizado para interpolación
    private var consecutiveDetections = 0
    private val MIN_CONSECUTIVE_DETECTIONS = 2 // Mínimas detecciones seguidas para considerar válido

    // Variables para manejar la persistencia temporal
    private var lastDetectionTime = 0L
    private var lastDetectedCode: String? = null
    private val DETECTION_PERSISTENCE_MS = 200L // Tiempo que mantenemos el último boundingBox válido
    private var persistentBoundingBox: Rect? = null
    private var persistentFormat: Int = 0

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val rotation = imageProxy.imageInfo.rotationDegrees
            val image = InputImage.fromMediaImage(mediaImage, rotation)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isEmpty()) {
                        // Si perdimos el código pero estamos dentro del tiempo de persistencia,
                        // mantenemos el último boundingBox conocido
                        if (currentTime - lastDetectionTime < DETECTION_PERSISTENCE_MS && persistentBoundingBox != null) {
                            handlePersistentResult(currentTime)
                        } else {
                            handleEmptyResult(currentTime)
                        }
                        return@addOnSuccessListener
                    }

                    val barcode = barcodes.first()
                    val currentCode = barcode.rawValue ?: ""
                    val currentBox = barcode.boundingBox?.let { originalBox ->
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

                    // Actualizar datos de persistencia
                    lastDetectionTime = currentTime
                    lastDetectedCode = currentCode
                    persistentBoundingBox = currentBox
                    persistentFormat = barcode.format

                    handleScanResult(currentTime, currentCode, currentBox, barcode.format)
                }
                .addOnFailureListener {
                    if (currentTime - lastDetectionTime < DETECTION_PERSISTENCE_MS && persistentBoundingBox != null) {
                        handlePersistentResult(currentTime)
                    } else {
                        handleEmptyResult(currentTime)
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun handlePersistentResult(currentTime: Long) {
        val timeForUIUpdate = currentTime - lastUIUpdateTime >= UI_UPDATE_DELAY_MS

        if (timeForUIUpdate && persistentBoundingBox != null && lastDetectedCode != null) {
            scope.launch {
                repository.onBarcodesDetected(
                    listOf(
                        BarcodeData(
                            value = lastDetectedCode!!,
                            format = persistentFormat,
                            boundingBox = persistentBoundingBox
                        )
                    )
                )
            }
            lastUIUpdateTime = currentTime
        }
    }

    private fun handleScanResult(currentTime: Long, currentCode: String, currentBox: Rect?, format: Int) {
        // Solo procesar si tenemos suficientes detecciones consecutivas
        if (consecutiveDetections < MIN_CONSECUTIVE_DETECTIONS) {
            return
        }

        val positionChanged = shouldUpdatePosition(currentBox)
        val timeForUIUpdate = currentTime - lastUIUpdateTime >= UI_UPDATE_DELAY_MS

        if (positionChanged && timeForUIUpdate) {
            // Aplicar suavizado al boundingBox
            val smoothedBox = currentBox?.let { newBox ->
                smoothBoundingBox?.let { prevBox ->
                    Rect(
                        (prevBox.left + (newBox.left - prevBox.left) * SMOOTHING_FACTOR).toInt(),
                        (prevBox.top + (newBox.top - prevBox.top) * SMOOTHING_FACTOR).toInt(),
                        (prevBox.right + (newBox.right - prevBox.right) * SMOOTHING_FACTOR).toInt(),
                        (prevBox.bottom + (newBox.bottom - prevBox.bottom) * SMOOTHING_FACTOR).toInt()
                    )
                } ?: newBox
            }

            scope.launch {
                repository.onBarcodesDetected(
                    listOf(
                        BarcodeData(
                            value = currentCode,
                            format = format,
                            boundingBox = smoothedBox
                        )
                    )
                )
            }
            lastUIUpdateTime = currentTime
            lastBoundingBox = currentBox
            smoothBoundingBox = smoothedBox
        }
    }

    private fun handleEmptyResult(currentTime: Long) {
        if (currentTime - lastUIUpdateTime >= UI_UPDATE_DELAY_MS) {
            scope.launch {
                repository.onBarcodesDetected(emptyList())
            }
            lastUIUpdateTime = currentTime
            smoothBoundingBox = null
            persistentBoundingBox = null
            lastDetectedCode = null
        }
    }

    private fun shouldUpdatePosition(newBox: Rect?): Boolean {
        if (newBox == null || lastBoundingBox == null) return true

        // Calcular el centro del boundingBox para mejor estabilidad
        val lastCenterX = (lastBoundingBox!!.left + lastBoundingBox!!.right) / 2
        val lastCenterY = (lastBoundingBox!!.top + lastBoundingBox!!.bottom) / 2
        val newCenterX = (newBox.left + newBox.right) / 2
        val newCenterY = (newBox.top + newBox.bottom) / 2

        return abs(newCenterX - lastCenterX) > MIN_POSITION_CHANGE ||
               abs(newCenterY - lastCenterY) > MIN_POSITION_CHANGE ||
               abs(newBox.width() - lastBoundingBox!!.width()) > MIN_POSITION_CHANGE ||
               abs(newBox.height() - lastBoundingBox!!.height()) > MIN_POSITION_CHANGE
    }
}
