package com.example.barcodescanner.features.barcodehistory.usecases

import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import com.example.barcodescanner.features.barcodehistory.domain.repository.BarcodeRepository
import com.example.barcodescanner.features.barcodehistory.domain.usecases.DeleteBarcodeUseCase
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.sql.Date

class DeleteBarcodeUseCaseTest {

    @MockK
    private lateinit var repository: BarcodeRepository

    private lateinit var deleteBarcodeUseCase: DeleteBarcodeUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        deleteBarcodeUseCase = DeleteBarcodeUseCase(repository)
        coEvery { repository.deleteBarcode(any()) } just Runs
    }

    @Test
    fun whenInvokeDeleteBarcodeUseCaseThenCallsDeleteBarcode() = runBlocking {
        val testBarcode = BarcodeEntity(id=1,code="123456789", timestamp = Date(System.currentTimeMillis()))
        deleteBarcodeUseCase(testBarcode)
        coVerify { repository.deleteBarcode(testBarcode) }
    }

    @Test(expected = Exception::class)
    fun whenInvokeDeleteBarcodeUseCaseWithErrorThenThrowsException() = runBlocking {
        // Simulate an error in the repository
        val testBarcode = BarcodeEntity(id=2,code="987654321", timestamp = Date(System.currentTimeMillis()))
        coEvery { repository.deleteBarcode(testBarcode) } throws Exception("Database error")

        // Call the use case and expect an exception
        deleteBarcodeUseCase(testBarcode)
    }
}