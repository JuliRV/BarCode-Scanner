package com.example.barcodescanner.features.barcodehistory

import com.example.barcodescanner.features.barcodehistory.domain.repository.BarcodeRepository
import com.example.barcodescanner.features.barcodehistory.domain.usecases.ClearHistoryUseCase
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ClearHistoryUseCaseTest {

    @MockK
    private lateinit var repository: BarcodeRepository
    private lateinit var clearHistoryUseCase: ClearHistoryUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearHistoryUseCase = ClearHistoryUseCase(repository)
    }

    @Test
    fun whenInvokeClearHistoryUseCaseThenCallsClearHistory() = runBlocking {
        // Arrange
        coEvery { repository.deleteAllBarcodes() } just Runs

        // Act
        clearHistoryUseCase()

        // Assert
        coVerify { repository.deleteAllBarcodes() }
    }

    @Test(expected = Exception::class)
    fun whenInvokeClearHistoryUseCaseWithErrorThenThrowsException() = runBlocking {
        // Arrange
        coEvery { repository.deleteAllBarcodes() } throws Exception("Database error")

        // Act
        clearHistoryUseCase()

        // Assert
        coVerify { repository.deleteAllBarcodes() }
    }
}