package com.example.barcodescanner.core.ui.home

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
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToScanner: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Menú Principal - Escáner de Códigos de Barras",
            modifier = Modifier.padding(bottom = 24.dp))
        Button(
            onClick = onNavigateToScanner,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Abrir Escáner de Códigos de Barras")
        }

        Button(
            onClick = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(text = "Ver Historial de Códigos")
        }
    }
}