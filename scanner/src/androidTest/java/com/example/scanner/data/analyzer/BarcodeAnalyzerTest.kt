package com.example.scanner.data.analyzer

import android.graphics.ImageFormat
import android.media.Image
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageInfo
import androidx.camera.core.ImageProxy
import com.example.scanner.data.repository.BarcodeScannerRepositoryImpl
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalGetImage::class)
class BarcodeAnalyzerTest {

    @MockK
    private lateinit var mockImageProxy: ImageProxy

    @MockK
    private lateinit var mockImageInfo: ImageInfo

    @MockK
    private lateinit var mockImage: Image

    @MockK
    private lateinit var mockScanner: BarcodeScanner

    private lateinit var repository: BarcodeScannerRepositoryImpl
    private lateinit var analyzer: BarcodeAnalyzer
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Configurar repository
        repository = mockk(relaxed = true)
        coJustRun { repository.onBarcodesDetected(any()) }

        // Configurar mock de BarcodeScanning
        mockkStatic(BarcodeScanning::class)
        every { BarcodeScanning.getClient() } returns mockScanner

        // Configurar mocks de imagen
        every { mockImageProxy.image } returns mockImage
        every { mockImageProxy.imageInfo } returns mockImageInfo
        every { mockImageInfo.rotationDegrees } returns 0
        every { mockImage.format } returns ImageFormat.YUV_420_888
        every { mockImage.width } returns 640
        every { mockImage.height } returns 480
        every { mockImage.planes } returns arrayOf(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true)
        )
        every { mockImageProxy.close() } just Runs

        analyzer = BarcodeAnalyzer(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun whenImageIsNullThenClosesImageProxy() {
        // Given
        every { mockImageProxy.image } returns null

        // When
        analyzer.analyze(mockImageProxy)

        // Then
        verify(exactly = 1) { mockImageProxy.close() }
        coVerify(inverse = true) { repository.onBarcodesDetected(any()) }
    }

    @Test
    fun whenScanSucceedsWithBarcodeThenEmitsBarcode() {
        // Given
        val expectedBarcode = mockk<Barcode>()
        every { expectedBarcode.rawValue } returns "123456789"
        every { expectedBarcode.format } returns Barcode.FORMAT_QR_CODE

        every { mockScanner.process(any<InputImage>()) } answers {
            val task = Tasks.forResult(listOf(expectedBarcode))
            task
        }

        // When
        analyzer.analyze(mockImageProxy)

        // Then
        coVerify(timeout = 1000) {
            repository.onBarcodesDetected(match { barcodes ->
                barcodes.size == 1 &&
                barcodes[0].value == "123456789" &&
                barcodes[0].format == Barcode.FORMAT_QR_CODE
            })
        }
        verify { mockImageProxy.close() }
    }

    @Test
    fun whenScanFailsThenEmitsEmptyList() {
        // Given
        every { mockScanner.process(any<InputImage>()) } answers {
            val task = Tasks.forException<List<Barcode>>(Exception("Test error"))
            task
        }

        // When
        analyzer.analyze(mockImageProxy)

        // Then
        coVerify(timeout = 1000) { repository.onBarcodesDetected(emptyList()) }
        verify { mockImageProxy.close() }
    }

    @Test
    fun whenMultipleBarcodesDetectedThenEmitsAllBarcodes() {
        // Given
        val barcodes = listOf(
            mockk<Barcode> {
                every { rawValue } returns "123456789"
                every { format } returns Barcode.FORMAT_QR_CODE
            },
            mockk<Barcode> {
                every { rawValue } returns "987654321"
                every { format } returns Barcode.FORMAT_CODE_128
            }
        )

        every { mockScanner.process(any<InputImage>()) } answers {
            val task = Tasks.forResult(barcodes)
            task
        }

        // When
        analyzer.analyze(mockImageProxy)

        // Then
        coVerify(timeout = 1000) {
            repository.onBarcodesDetected(match { detectedBarcodes ->
                detectedBarcodes.size == 2 &&
                detectedBarcodes[0].value == "123456789" &&
                detectedBarcodes[0].format == Barcode.FORMAT_QR_CODE &&
                detectedBarcodes[1].value == "987654321" &&
                detectedBarcodes[1].format == Barcode.FORMAT_CODE_128
            })
        }
        verify { mockImageProxy.close() }
    }
}
