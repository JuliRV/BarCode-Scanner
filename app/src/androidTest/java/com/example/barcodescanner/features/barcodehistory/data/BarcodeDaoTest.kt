package com.example.barcodescanner.features.barcodehistory.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.barcodescanner.features.barcodehistory.data.dao.BarcodeDao
import com.example.barcodescanner.features.barcodehistory.data.database.BarcodeDatabase
import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import io.mockk.coVerify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class BarcodeDaoTest {

    private lateinit var database: BarcodeDatabase // Base de datos que contiene el DAO (no se mockea)
    private lateinit var barcodeDao: BarcodeDao

    @Before
    fun setUp() {
        // Initialize the database and DAO here
        // This is usually done with Room's in-memory database for testing
        // Crear base datos en memoria para hacer el testing:
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BarcodeDatabase::class.java
        ).allowMainThreadQueries() // Permite queries en el hilo principal (solo para testing)
            .build()
        barcodeDao = database.barcodeDao()
    }

    @After
    fun tearDown() {
        // Cerrar la base de datos después de cada prueba/test antes de la siguiente
        database.close()
    }

    @Test
    fun whenInsertBarcodeThenIsStoredInDatabase()= runBlocking{
        var barcode = BarcodeEntity(id=1, code = "123456789", timestamp = Date(System.currentTimeMillis()))
        barcodeDao.insertBarcode(barcode)
        val storedBarcode = barcodeDao.getAllBarcodes().first()
        assertEquals(1, storedBarcode.size)
        assertEquals("123456789", storedBarcode.first().code)
        assertEquals(1, storedBarcode.first().id)
        assertEquals(barcode.timestamp, storedBarcode.first().timestamp)
    }

    @Test
    fun whenGetAllBarcodesThenReturnsAllStoredBarcodes() = runBlocking {
        val barcode1 = BarcodeEntity(id = 1, code = "123456789", timestamp = Date(System.currentTimeMillis()))
        val barcode2 = BarcodeEntity(id = 2, code = "987654321", timestamp = Date(System.currentTimeMillis()))

        barcodeDao.insertBarcode(barcode1)
        barcodeDao.insertBarcode(barcode2)

        val storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(2, storedBarcodes.size)
        assertEquals("123456789", storedBarcodes[0].code)
        assertEquals("987654321", storedBarcodes[1].code)
    }

    @Test
    fun whenDeleteBarcodeThenBarcodeIsRemoved() = runBlocking {
        val barcode = BarcodeEntity(id = 1, code = "123456789", timestamp = Date(System.currentTimeMillis()))
        barcodeDao.insertBarcode(barcode)

        // Verificar que el código se ha insertado primero
        var storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(1, storedBarcodes.size)

        // Elimino el código
        barcodeDao.deleteBarcode(barcode)

        // Por ultimo verifico que el código se ha eliminado correctamente:
        storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(0, storedBarcodes.size)
    }

    @Test
    fun whenClearHistoryThenAllStoredBarcodesAreDeletedFromDatabase() = runBlocking {
        val barcode1 = BarcodeEntity(id = 1, code = "123456789", timestamp = Date(System.currentTimeMillis()))
        val barcode2 = BarcodeEntity(id = 2, code = "987654321", timestamp = Date(System.currentTimeMillis()))

        barcodeDao.insertBarcode(barcode1)
        barcodeDao.insertBarcode(barcode2)

        var storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(2, storedBarcodes.size)

        barcodeDao.deleteAllBarcodes()

        storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(0, storedBarcodes.size)
    }

    @Test
    fun whenInsertDuplicateIdThenLastOneIsKept() = runBlocking {
        val barcode1 = BarcodeEntity(id = 1, code = "123456789", timestamp = Date(System.currentTimeMillis()))
        val barcode2 = BarcodeEntity(id = 1, code = "987654321", timestamp = Date(System.currentTimeMillis()))

        barcodeDao.insertBarcode(barcode1)
        barcodeDao.insertBarcode(barcode2)

        val storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(1, storedBarcodes.size)
        assertEquals("987654321", storedBarcodes.first().code)
    }

    @Test
    fun whenGetAllBarcodesThenReturnedInTimestampOrder() = runBlocking {
        val timestamp1 = Date(System.currentTimeMillis())
        Thread.sleep(100) // Asegurar diferentes timestamps
        val timestamp2 = Date(System.currentTimeMillis())
        Thread.sleep(100)
        val timestamp3 = Date(System.currentTimeMillis())

        val barcode3 = BarcodeEntity(id = 3, code = "333", timestamp = timestamp3)
        val barcode1 = BarcodeEntity(id = 1, code = "111", timestamp = timestamp1)
        val barcode2 = BarcodeEntity(id = 2, code = "222", timestamp = timestamp2)

        // Insertar en orden aleatorio
        barcodeDao.insertBarcode(barcode2)
        barcodeDao.insertBarcode(barcode1)
        barcodeDao.insertBarcode(barcode3)

        val storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(3, storedBarcodes.size)
        // Verificar orden descendente por timestamp
        assertEquals("333", storedBarcodes[0].code)
        assertEquals("222", storedBarcodes[1].code)
        assertEquals("111", storedBarcodes[2].code)
    }

    @Test
    fun whenInsertMultipleCodesThenAllAreStored() = runBlocking {
        val barcodes = (1..5).map {
            BarcodeEntity(
                id = it.toLong(),
                code = "code$it",
                timestamp = Date(System.currentTimeMillis() + it)
            )
        }

        barcodes.forEach { barcodeDao.insertBarcode(it) }

        val storedBarcodes = barcodeDao.getAllBarcodes().first()
        assertEquals(5, storedBarcodes.size)
        // Verificar que todos los códigos están almacenados
        barcodes.forEach { barcode ->
            assertTrue(storedBarcodes.any { it.code == barcode.code })
        }
    }
}