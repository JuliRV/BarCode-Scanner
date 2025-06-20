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
}