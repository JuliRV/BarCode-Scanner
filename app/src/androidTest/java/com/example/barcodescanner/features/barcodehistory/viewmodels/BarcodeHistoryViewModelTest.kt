package com.example.barcodescanner.features.barcodehistory.viewmodels

import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import com.example.barcodescanner.features.barcodehistory.domain.usecases.ClearHistoryUseCase
import com.example.barcodescanner.features.barcodehistory.domain.usecases.DeleteBarcodeUseCase
import com.example.barcodescanner.features.barcodehistory.domain.usecases.GetBarcodesUseCase
import com.example.barcodescanner.features.barcodehistory.presentation.history.BarcodeHistoryViewModel
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.sql.Date

@OptIn(ExperimentalCoroutinesApi::class)
class BarcodeHistoryViewModelTest {

    @MockK(relaxed = true)
    private lateinit var getBarcodesUseCase: GetBarcodesUseCase

    @MockK(relaxed = true)
    private lateinit var deleteBarcodeUseCase: DeleteBarcodeUseCase

    @MockK(relaxed = true)
    private lateinit var clearHistoryUseCase: ClearHistoryUseCase

    private lateinit var viewModel: BarcodeHistoryViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        // Simula un flujo con un valor inicial vacío.
        every { getBarcodesUseCase() } returns flowOf(emptyList())
        viewModel = BarcodeHistoryViewModel(getBarcodesUseCase, deleteBarcodeUseCase, clearHistoryUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testDeleteBarcode() = runTest {
        val testBarcode = BarcodeEntity(id = 1, code = "123456789", timestamp = Date(System.currentTimeMillis()))
        viewModel.deleteBarcode(testBarcode)
        // Ejecuta pendientes en viewModelScope
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { deleteBarcodeUseCase(testBarcode) }
    }

    @Test
    fun testClearHistory() = runTest {
        viewModel.clearHistory()
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { clearHistoryUseCase() }
    }

}