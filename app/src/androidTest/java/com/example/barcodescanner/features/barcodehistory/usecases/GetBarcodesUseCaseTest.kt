package com.example.barcodescanner.features.barcodehistory.usecases

import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import com.example.barcodescanner.features.barcodehistory.domain.repository.BarcodeRepository
import com.example.barcodescanner.features.barcodehistory.domain.usecases.GetBarcodesUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class GetBarcodesUseCaseTest {

    @MockK
    private lateinit var repository: BarcodeRepository

    private lateinit var getBarcodesUseCase: GetBarcodesUseCase

    @Before
    fun setUp(){
        MockKAnnotations.init(this)
        getBarcodesUseCase = GetBarcodesUseCase(repository)
    }

    @Test
    fun whenInvokeGetBarcodesUseCaseThenReturnsFlowOfBarcodes()= runBlocking{
        val barcode = BarcodeEntity(id = 1, code = "123456789", timestamp = Date(System.currentTimeMillis()))
        coEvery { repository.getAllBarcodes()  } returns flowOf(listOf(barcode))

        val result = getBarcodesUseCase().first()
        assertEquals(1, result.size)
        assertEquals("123456789", result.first().code)
        assertEquals(barcode.id, result.first().id)
        assertEquals(barcode.timestamp, result.first().timestamp)


    }
    @Test
    fun whenInvokeGetBarcodesUseCaseThenReturnsEmptyList() = runBlocking {
        coEvery { repository.getAllBarcodes() } returns flowOf(emptyList())

        val result = getBarcodesUseCase().toList()
        assertEquals(1, result.size)
        assertEquals(0, result.first().size)
    }

    @Test
    fun whenInvokeGetBarcodesUseCaseThenReturnsMultipleBarcodes() = runBlocking {
        val barcode1 = BarcodeEntity(id = 1, code = "123456789", timestamp = Date(System.currentTimeMillis()))
        val barcode2 = BarcodeEntity(id = 2, code = "987654321", timestamp = Date(System.currentTimeMillis()))
        coEvery { repository.getAllBarcodes() } returns flowOf(listOf(barcode1, barcode2))

        val result = getBarcodesUseCase().first()
        assertEquals(2, result.size)
        assertEquals("123456789", result[0].code)
        assertEquals("987654321", result[1].code)
    }

}