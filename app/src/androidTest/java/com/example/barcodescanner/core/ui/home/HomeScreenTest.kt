package com.example.barcodescanner.core.ui.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysAllElements() {
        // Arrange: Configurar la pantalla
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToScanner = {},
                onNavigateToHistory = {}
            )
        }

        // Assert: Verificar que todos los elementos están presentes
        composeTestRule.onNodeWithTag("homeScreen").assertExists()
        composeTestRule.onNodeWithTag("titleText").assertExists()
        composeTestRule.onNodeWithTag("scannerButton").assertExists()
        composeTestRule.onNodeWithTag("historyButton").assertExists()
        composeTestRule.onNodeWithTag("espressoButton").assertExists()
    }

    @Test
    fun homeScreen_titleText_hasCorrectContent() {
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToScanner = {},
                onNavigateToHistory = {}
            )
        }

        // Verificar el contenido del texto
        composeTestRule.onNodeWithTag("titleText")
            .assertTextContains("Menú Principal", substring = true)
    }

    @Test
    fun homeScreen_scannerButton_isClickable() {
        var scannerClicked = false

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToScanner = { scannerClicked = true },
                onNavigateToHistory = {}
            )
        }

        // Act: Hacer clic en el botón
        composeTestRule.onNodeWithTag("scannerButton").performClick()

        // Assert: Verificar que se ejecutó el callback
        assert(scannerClicked)
    }

    @Test
    fun homeScreen_historyButton_isClickable() {
        var historyClicked = false

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToScanner = {},
                onNavigateToHistory = { historyClicked = true }
            )
        }

        // Act: Hacer clic en el botón
        composeTestRule.onNodeWithTag("historyButton").performClick()

        // Assert: Verificar que se ejecutó el callback
        assert(historyClicked)
    }

    @Test
    fun homeScreen_allButtons_areDisplayed() {
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToScanner = {},
                onNavigateToHistory = {}
            )
        }

        // Verificar que todos los botones son visibles
        composeTestRule.onNodeWithTag("scannerButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("historyButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("espressoButton").assertIsDisplayed()
    }
}