package com.example.barcodescanner.features.barcodehistory.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "barcodes")
data class BarcodeEntity(
    @PrimaryKey
    val code: String,
    val timestamp: Date
)
