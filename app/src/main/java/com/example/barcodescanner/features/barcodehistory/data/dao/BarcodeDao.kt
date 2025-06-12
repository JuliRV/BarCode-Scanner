package com.example.barcodescanner.features.barcodehistory.data.dao

import androidx.room.*
import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BarcodeDao {
    @Query("SELECT * FROM barcodes ORDER BY timestamp DESC")
    fun getAllBarcodes(): Flow<List<BarcodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarcode(barcode: BarcodeEntity)

    @Query("DELETE FROM barcodes")
    suspend fun deleteAllBarcodes()

    @Delete
    suspend fun deleteBarcode(barcode: BarcodeEntity)
}
