package com.example.barcodescanner.espresso

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.core.R

class EspressoTestActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_espresso_test)

        val button = findViewById<Button>(R.id.actionButton)
        button.setOnClickListener {
            Toast.makeText(this, "Botón presionado: acción de prueba", Toast.LENGTH_SHORT).show()
        }
    }
}
