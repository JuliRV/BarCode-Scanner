package com.example.scanner.domain.model

import android.graphics.Rect

data class BarcodeData(
    val value: String,
    val format: Int,
    val boundingBox: Rect? = null
)
