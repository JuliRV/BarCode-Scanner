package com.example.scanner.ui

import android.graphics.Rect
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.scanner.domain.model.BarcodeData
import com.example.scanner.domain.repository.BarcodeScannerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScannerScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private class MockBarcodeScannerRepository : BarcodeScannerRepository {
        private val _barcodesFlow = MutableStateFlow<List<BarcodeData>>(emptyList())

        override fun analyzeImageFlow(): Flow<List<BarcodeData>> = _barcodesFlow

        fun emitBarcodes(barcodes: List<BarcodeData>) {
            _barcodesFlow.value = barcodes
        }
    }

    private class MockAnalyzer : androidx.camera.core.ImageAnalysis.Analyzer {
        override fun analyze(image: androidx.camera.core.ImageProxy) {
            image.close()
        }
    }

    @Test
    fun scannerScreen_displaysBasicElements() {
        composeRule.setContent {
            ScannerScreen(
                onBack = {},
                onBarcodeScanned = {},
                viewModel = ScannerViewModel(
                    repository = MockBarcodeScannerRepository(),
                    analyzer = MockAnalyzer()
                )
            )
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag("scannerScreen").assertExists()
        composeRule.onNodeWithTag("topControlsRow").assertExists()
        composeRule.onNodeWithTag("backButton").assertIsDisplayed()
        composeRule.onNodeWithText("Volver al Menú Principal").assertIsDisplayed()
        composeRule.onNodeWithText("Modo Avanzado").assertIsDisplayed()
        composeRule.onNodeWithTag("advancedModeSwitch").assertExists()
    }

    @Test
    fun scannerScreen_backButton_isClickable() {
        var backClicked = false

        composeRule.setContent {
            ScannerScreen(
                onBack = { backClicked = true },
                onBarcodeScanned = {},
                viewModel = ScannerViewModel(
                    repository = MockBarcodeScannerRepository(),
                    analyzer = MockAnalyzer()
                )
            )
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("backButton").performClick()
        assert(backClicked) { "El botón de volver debería ejecutar el callback" }
    }

    @Test
    fun scannerScreen_advancedModeSwitch_togglesMode() {
        val repository = MockBarcodeScannerRepository()
        val viewModel = ScannerViewModel(
            repository = repository,
            analyzer = MockAnalyzer()
        )

        composeRule.setContent {
            ScannerScreen(
                onBack = {},
                onBarcodeScanned = {},
                viewModel = viewModel
            )
        }

        composeRule.waitForIdle()

        assert(!viewModel.isAdvancedMode.value)

        composeRule.onNodeWithTag("advancedModeSwitch").performClick()
        composeRule.waitForIdle()

        assert(viewModel.isAdvancedMode.value)

        composeRule.onNodeWithTag("advancedModeSwitch").performClick()
        composeRule.waitForIdle()

        assert(!viewModel.isAdvancedMode.value)
    }

    @Ignore("Requiere permisos de cámara reales")
    @Test
    fun scannerScreen_displaysBarcodeList_inSimpleMode() {
        val repository = MockBarcodeScannerRepository()
        val testBarcodes = listOf(
            BarcodeData("123456", 1, Rect()),
            BarcodeData("789012", 2, Rect())
        )
        repository.emitBarcodes(testBarcodes)

        val viewModel = ScannerViewModel(
            repository = repository,
            analyzer = MockAnalyzer()
        )

        composeRule.setContent {
            ScannerScreen(
                onBack = {},
                onBarcodeScanned = {},
                viewModel = viewModel
            )
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag("cameraPreviewBox").assertExists()
        composeRule.onNodeWithTag("barcodeList").assertExists()
        composeRule.onNodeWithText("Código: 123456 (formato: 1)", substring = true).assertExists()
        composeRule.onNodeWithText("Código: 789012 (formato: 2)", substring = true).assertExists()
    }

    @Ignore("Requiere permisos de cámara reales")
    @Test
    fun scannerScreen_displaysSelectedBarcode_inAdvancedMode() {
        val repository = MockBarcodeScannerRepository()
        val testBarcode = BarcodeData("ADVANCED123", 1, Rect(0, 0, 100, 100))
        repository.emitBarcodes(listOf(testBarcode))

        val viewModel = ScannerViewModel(
            repository = repository,
            analyzer = MockAnalyzer()
        )

        composeRule.setContent {
            ScannerScreen(
                onBack = {},
                onBarcodeScanned = {},
                viewModel = viewModel
            )
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag("advancedModeSwitch").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("selectedBarcodeCard").assertExists()
        composeRule.onNodeWithTag("selectedBarcodeText").assertTextEquals("ADVANCED123")
        composeRule.onNodeWithTag("deleteButton").assertExists()
    }

    @Ignore("Requiere permisos de cámara reales")
    @Test
    fun scannerScreen_deleteButton_triggersCallback_inAdvancedMode() {
        val repository = MockBarcodeScannerRepository()
        val testBarcode = BarcodeData("DELETE_ME", 1, Rect(0, 0, 100, 100))
        var scannedBarcode: BarcodeData? = null

        repository.emitBarcodes(listOf(testBarcode))

        val viewModel = ScannerViewModel(
            repository = repository,
            analyzer = MockAnalyzer()
        )

        composeRule.setContent {
            ScannerScreen(
                onBack = {},
                onBarcodeScanned = { scannedBarcode = it },
                viewModel = viewModel
            )
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag("advancedModeSwitch").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("deleteButton").assertExists()
        composeRule.onNodeWithTag("deleteButton").performClick()
        composeRule.waitForIdle()

        assert(scannedBarcode != null)
        assert(scannedBarcode?.value == "DELETE_ME")
    }

    @Ignore("Requiere permisos de cámara reales")
    @Test
    fun scannerScreen_cameraPreviewBox_existsWithPermission() {
        composeRule.setContent {
            ScannerScreen(
                onBack = {},
                onBarcodeScanned = {},
                viewModel = ScannerViewModel(
                    repository = MockBarcodeScannerRepository(),
                    analyzer = MockAnalyzer()
                )
            )
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag("cameraPreviewBox").assertExists()
    }

    @Ignore("Requiere permisos de cámara reales")
    @Test
    fun scannerScreen_switchingModes_hidesAndShowsCorrectElements() {
        val repository = MockBarcodeScannerRepository()
        val testBarcodes = listOf(
            BarcodeData("MODE_TEST", 1, Rect(0, 0, 100, 100))
        )
        repository.emitBarcodes(testBarcodes)

        val viewModel = ScannerViewModel(
            repository = repository,
            analyzer = MockAnalyzer()
        )

        composeRule.setContent {
            ScannerScreen(
                onBack = {},
                onBarcodeScanned = {},
                viewModel = viewModel
            )
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag("barcodeList").assertExists()

        composeRule.onNodeWithTag("advancedModeSwitch").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("selectedBarcodeCard").assertExists()
        composeRule.onNodeWithTag("barcodeList").assertDoesNotExist()

        composeRule.onNodeWithTag("advancedModeSwitch").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("barcodeList").assertExists()
        composeRule.onNodeWithTag("selectedBarcodeCard").assertDoesNotExist()
    }

    @Ignore("Requiere permisos de cámara reales")
    @Test
    fun scannerScreen_emptyBarcodeList_showsInSimpleMode() {
        val repository = MockBarcodeScannerRepository()

        val viewModel = ScannerViewModel(
            repository = repository,
            analyzer = MockAnalyzer()
        )

        composeRule.setContent {
            ScannerScreen(
                onBack = {},
                onBarcodeScanned = {},
                viewModel = viewModel
            )
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag("barcodeList").assertExists()
        composeRule.onNodeWithText("Código:", substring = true).assertDoesNotExist()
    }
}