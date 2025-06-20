package com.example.barcodescanner.features.barcodehistory.usecases

import com.example.barcodescanner.features.barcodehistory.domain.repository.BarcodeRepository
import com.example.barcodescanner.features.barcodehistory.domain.usecases.SaveBarcodeUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SaveBarcodeUseCaseTest {

    @MockK
    private lateinit var repository: BarcodeRepository

    private lateinit var saveBarcodeUseCase: SaveBarcodeUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        saveBarcodeUseCase = SaveBarcodeUseCase(repository)
    }

    @Test
    fun whenInvokeSaveBarcodeUseCaseThenCallsInsertBarcode() = runBlocking {
        val testCode = "123456"
        saveBarcodeUseCase(testCode)
        coVerify { repository.insertBarcode(match { it.code == testCode }) }
    }

    @Test(expected = Exception::class)
    fun whenInvokeSaveBarcodeUseCaseWithErrorThenThrowsException() = runBlocking {
        // Simulate an error in the repository
        coEvery { repository.insertBarcode(any()) } throws Exception("Database error")
        // Call the use case and expect an exception
        saveBarcodeUseCase("errorCode")
    }
}