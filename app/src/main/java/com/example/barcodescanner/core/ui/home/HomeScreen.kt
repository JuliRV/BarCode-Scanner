package com.example.barcodescanner.core.ui.home

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.barcodescanner.espresso.EspressoTestActivity

@Composable
fun HomeScreen(
    onNavigateToScanner: () -> Unit,
    onNavigateToHistory: () -> Unit
) {

    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .testTag("homeScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Menú Principal - Escáner de Códigos de Barras",
            modifier = Modifier.padding(bottom = 24.dp).testTag("titleText"))
        Button(
            onClick = onNavigateToScanner,
            modifier = Modifier.fillMaxWidth().testTag("scannerButton")
        ) {
            Text(text = "Abrir Escáner de Códigos de Barras")
        }

        Button(
            onClick = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).testTag("historyButton")
        ) {
            Text(text = "Ver Historial de Códigos")
        }

        //  NUEVO BOTÓN para probar Espresso
        Button(
            onClick = {
                val intent = Intent(context, EspressoTestActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).testTag("espressoButton")
        ) {
            Text(text = "Probar pantalla Espresso")
        }
    }
}