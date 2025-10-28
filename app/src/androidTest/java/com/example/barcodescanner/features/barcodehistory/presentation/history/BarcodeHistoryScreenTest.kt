package com.example.barcodescanner.features.barcodehistory.presentation.history

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import com.example.barcodescanner.features.barcodehistory.domain.usecases.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class BarcodeHistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun historyScreen_showsEmptyState_whenNoBarodes() {
        // Arrange: Crear ViewModel con lista vacía
        val getBarcodesUseCase = mockk<GetBarcodesUseCase>()
        val deleteUseCase = mockk<DeleteBarcodeUseCase>(relaxed = true)
        val clearUseCase = mockk<ClearHistoryUseCase>(relaxed = true)

        every { getBarcodesUseCase() } returns MutableStateFlow(emptyList())

        val viewModel = BarcodeHistoryViewModel(
            getBarcodesUseCase,
            deleteUseCase,
            clearUseCase
        )

        composeTestRule.setContent {
            BarcodeHistoryScreen(viewModel = viewModel)
        }

        // Assert: Verificar estado vacío
        composeTestRule.onNodeWithTag("emptyStateBox").assertExists()
        composeTestRule.onNodeWithTag("emptyStateText")
            .assertTextEquals("No hay códigos escaneados")
    }

    @Test
    fun historyScreen_displaysBarcodes_whenDataExists() {
        // Arrange: Crear lista de códigos de prueba
        val testBarcodes = listOf(
            BarcodeEntity(id = 1, code = "123456", timestamp = Date()),
            BarcodeEntity(id = 2, code = "789012", timestamp = Date())
        )

        val getBarcodesUseCase = mockk<GetBarcodesUseCase>()
        val deleteUseCase = mockk<DeleteBarcodeUseCase>(relaxed = true)
        val clearUseCase = mockk<ClearHistoryUseCase>(relaxed = true)

        every { getBarcodesUseCase() } returns MutableStateFlow(testBarcodes)

        val viewModel = BarcodeHistoryViewModel(
            getBarcodesUseCase,
            deleteUseCase,
            clearUseCase
        )

        composeTestRule.setContent {
            BarcodeHistoryScreen(viewModel = viewModel)
        }

        // Assert: Verificar que se muestran los códigos
        composeTestRule.onNodeWithTag("barcodeList").assertExists()
        composeTestRule.onNodeWithTag("barcodeItem_1").assertExists()
        composeTestRule.onNodeWithTag("barcodeItem_2").assertExists()
    }

    @Test
    fun historyScreen_clearButton_exists() {
        val getBarcodesUseCase = mockk<GetBarcodesUseCase>()
        val deleteUseCase = mockk<DeleteBarcodeUseCase>(relaxed = true)
        val clearUseCase = mockk<ClearHistoryUseCase>(relaxed = true)

        every { getBarcodesUseCase() } returns MutableStateFlow(emptyList())

        val viewModel = BarcodeHistoryViewModel(
            getBarcodesUseCase,
            deleteUseCase,
            clearUseCase
        )

        composeTestRule.setContent {
            BarcodeHistoryScreen(viewModel = viewModel)
        }

        // Verificar que el botón de limpiar existe
        composeTestRule.onNodeWithTag("clearHistoryButton").assertExists()
    }
}