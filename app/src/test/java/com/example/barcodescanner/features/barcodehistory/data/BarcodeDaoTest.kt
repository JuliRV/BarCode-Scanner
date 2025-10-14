package com.example.barcodescanner.features.barcodehistory.data

import com.example.barcodescanner.features.barcodehistory.data.dao.BarcodeDao
import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.Runs
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class BarcodeDaoTest {

    @MockK
    private lateinit var barcodeDao: BarcodeDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun whenInsertBarcodeThenIsStoredInDatabase() = runBlocking {
        val barcode = BarcodeEntity(id = 1, code = "123456789", timestamp = Date(System.currentTimeMillis()))

        coEvery { barcodeDao.insertBarcode(barcode) } just Runs
        coEvery { barcodeDao.getAllBarcodes() } returns flowOf(listOf(barcode))

        barcodeDao.insertBarcode(barcode)
        val storedBarcode = barcodeDao.getAllBarcodes().first()

        assertEquals(1, storedBarcode.size)
        assertEquals("123456789", storedBarcode.first().code)
        assertEquals(1, storedBarcode.first().id)
        coVerify { barcodeDao.insertBarcode(barcode) }
    }

    @Test
    fun whenGetAllBarcodesThenReturnsAllStoredBarcodes() = runBlocking {
        val timestamp1 = Date(System.currentTimeMillis())
        val timestamp2 = Date(System.currentTimeMillis() + 1000)

        val barcode1 = BarcodeEntity(id = 1, code = "123456789", timestamp = timestamp1)
        val barcode2 = BarcodeEntity(id = 2, code = "987654321", timestamp = timestamp2)

        coEvery { barcodeDao.getAllBarcodes() } returns flowOf(listOf(barcode2, barcode1))

        val storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(2, storedBarcodes.size)
        assertEquals("987654321", storedBarcodes[0].code)
        assertEquals("123456789", storedBarcodes[1].code)
    }

    @Test
    fun whenDeleteBarcodeThenBarcodeIsRemoved() = runBlocking {
        val barcode = BarcodeEntity(id = 1, code = "123456789", timestamp = Date(System.currentTimeMillis()))

        coEvery { barcodeDao.deleteBarcode(barcode) } just Runs
        coEvery { barcodeDao.getAllBarcodes() } returns flowOf(emptyList())

        barcodeDao.deleteBarcode(barcode)

        val storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(0, storedBarcodes.size)
        coVerify { barcodeDao.deleteBarcode(barcode) }
    }

    @Test
    fun whenClearHistoryThenAllStoredBarcodesAreDeletedFromDatabase() = runBlocking {
        coEvery { barcodeDao.deleteAllBarcodes() } just Runs
        coEvery { barcodeDao.getAllBarcodes() } returns flowOf(emptyList())

        barcodeDao.deleteAllBarcodes()

        val storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(0, storedBarcodes.size)
        coVerify { barcodeDao.deleteAllBarcodes() }
    }

    @Test
    fun whenInsertDuplicateIdThenLastOneIsKept() = runBlocking {
        val barcode2 = BarcodeEntity(id = 1, code = "987654321", timestamp = Date(System.currentTimeMillis()))

        coEvery { barcodeDao.insertBarcode(any()) } just Runs
        coEvery { barcodeDao.getAllBarcodes() } returns flowOf(listOf(barcode2))

        barcodeDao.insertBarcode(barcode2)

        val storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(1, storedBarcodes.size)
        assertEquals("987654321", storedBarcodes.first().code)
    }

    @Test
    fun whenGetAllBarcodesThenReturnedInTimestampOrder() = runBlocking {
        val timestamp1 = Date(System.currentTimeMillis())
        val timestamp2 = Date(System.currentTimeMillis() + 1000)
        val timestamp3 = Date(System.currentTimeMillis() + 2000)

        val barcode3 = BarcodeEntity(id = 3, code = "333", timestamp = timestamp3)
        val barcode2 = BarcodeEntity(id = 2, code = "222", timestamp = timestamp2)
        val barcode1 = BarcodeEntity(id = 1, code = "111", timestamp = timestamp1)

        coEvery { barcodeDao.getAllBarcodes() } returns flowOf(listOf(barcode3, barcode2, barcode1))

        val storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(3, storedBarcodes.size)
        assertEquals("333", storedBarcodes[0].code)
        assertEquals("222", storedBarcodes[1].code)
        assertEquals("111", storedBarcodes[2].code)
    }

    @Test
    fun whenInsertMultipleCodesThenAllAreStored() = runBlocking {
        val barcodes = (1..5).map {
            BarcodeEntity(
                id = it,
                code = "code$it",
                timestamp = Date(System.currentTimeMillis() + it)
            )
        }

        coEvery { barcodeDao.insertBarcode(any()) } just Runs
        coEvery { barcodeDao.getAllBarcodes() } returns flowOf(barcodes)

        barcodes.forEach { barcodeDao.insertBarcode(it) }

        val storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(5, storedBarcodes.size)
        barcodes.forEach { barcode ->
            assertTrue(storedBarcodes.any { it.code == barcode.code })
        }
    }
}