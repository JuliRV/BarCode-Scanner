package com.example.barcodescanner.features.barcodehistory.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.barcodescanner.features.barcodehistory.data.dao.BarcodeDao
import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import com.example.barcodescanner.features.barcodehistory.data.utils.DateConverter

@Database(entities = [BarcodeEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class BarcodeDatabase : RoomDatabase() {
    abstract fun barcodeDao(): BarcodeDao
}
