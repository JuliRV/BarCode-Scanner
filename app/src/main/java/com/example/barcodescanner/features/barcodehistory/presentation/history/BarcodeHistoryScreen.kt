package com.example.barcodescanner.features.barcodehistory.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.barcodescanner.features.barcodehistory.data.entities.BarcodeEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeHistoryScreen(
    viewModel: BarcodeHistoryViewModel = hiltViewModel()
) {
    val barcodes by viewModel.barcodes.collectAsState()

    Scaffold(
        modifier = Modifier.testTag("historyScreen"),
        topBar = {
            TopAppBar(
                title = { Text("Historial de códigos") },
                actions = {
                    IconButton(onClick = { viewModel.clearHistory() },
                        modifier = Modifier.testTag("clearHistoryButton")) {
                        Icon(Icons.Default.Delete, contentDescription = "Limpiar historial")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (barcodes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("emptyStateBox"),
                contentAlignment = Alignment.Center
            ) {
                Text(text=("No hay códigos escaneados"), modifier = Modifier.testTag("emptyStateText"))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("barcodeList"),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(barcodes) { barcode ->
                    BarcodeItem(
                        barcode = barcode,
                        onDelete = { viewModel.deleteBarcode(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun BarcodeItem(
    barcode: BarcodeEntity,
    onDelete: (BarcodeEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("barcodeItem_${barcode.id}"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = barcode.code,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("barcodeText_${barcode.id}")
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(barcode.timestamp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("barcodeDate_${barcode.id}")
                )
            }
            IconButton(onClick = { onDelete(barcode) },
                modifier = Modifier.testTag("deleteButton_${barcode.id}")) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar código",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
