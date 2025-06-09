package com.example.scanner.data.model

data class BarcodeResult(
    val value: String,
    val format: Int,
    val boundingBox: android.graphics.Rect?
)
