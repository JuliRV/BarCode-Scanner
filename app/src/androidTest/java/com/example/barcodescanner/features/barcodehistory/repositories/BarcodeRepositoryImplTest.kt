package com.example.barcodescanner.features.barcodehistory.repositories

import com.example.barcodescanner.features.barcodehistory.data.dao.BarcodeDao
import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import com.example.barcodescanner.features.barcodehistory.data.repository.BarcodeRepositoryImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class BarcodeRepositoryImplTest {

    @MockK
    private lateinit var barcodeDao: BarcodeDao

    private lateinit var repository: BarcodeRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = BarcodeRepositoryImpl(barcodeDao)
    }

    @Test
    fun whenInsertBarcodeThenCallsDaoMethod() = runBlocking {
        val barcode = BarcodeEntity(id = 1, code = "123456789", timestamp = Date(System.currentTimeMillis()))
        coEvery { barcodeDao.insertBarcode(barcode) } returns Unit

        repository.insertBarcode(barcode)

        coVerify { barcodeDao.insertBarcode(barcode) }
    }

    @Test
    fun whenGetAllBarcodesThenReturnsFlowOfBarcodes() = runBlocking {
        val barcode = BarcodeEntity(id = 2, code = "987654321", timestamp = Date(System.currentTimeMillis()))
        coEvery { barcodeDao.getAllBarcodes() } returns flowOf(listOf(barcode))

        val result = repository.getAllBarcodes()
        result.collect { list ->
            assertEquals(1, list.size)
            assertEquals("987654321", list.first().code)
        }
    }

    @Test
    fun whenDeleteBarcodeThenCallsDaoMethod() = runBlocking {
        val barcode = BarcodeEntity(id = 3, code = "123123123", timestamp = Date(System.currentTimeMillis()))
        coEvery { barcodeDao.deleteBarcode(barcode) } returns Unit

        repository.deleteBarcode(barcode)

        coVerify { barcodeDao.deleteBarcode(barcode) }
    }

    @Test
    fun whenDeleteAllBarcodesThenCallsDaoMethod() = runBlocking {
        coEvery { barcodeDao.deleteAllBarcodes() } returns Unit

        repository.deleteAllBarcodes()

        coVerify { barcodeDao.deleteAllBarcodes() }
    }
}