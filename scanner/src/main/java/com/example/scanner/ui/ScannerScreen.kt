package com.example.scanner.ui

import android.content.pm.PackageManager
import android.util.Size
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scanner.domain.model.BarcodeData

@Composable
fun ScannerScreen(
    onBack: () -> Unit,
    onBarcodeScanned: (BarcodeData) -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val barcodes by viewModel.barcodeFlow.collectAsState(initial = emptyList())
    val isAdvancedMode by viewModel.isAdvancedMode.collectAsState()
    val selectedBarcode by viewModel.selectedBarcode.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Volver al Menú Principal")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Modo Avanzado")
                Switch(
                    checked = isAdvancedMode,
                    onCheckedChange = { viewModel.toggleAdvancedMode() }
                )
            }
        }

        if (hasCameraPermission) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                CameraPreview(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )

                if (isAdvancedMode && selectedBarcode != null) {
                    val context = LocalContext.current
                    val windowManager = context.getSystemService(android.content.Context.WINDOW_SERVICE) as android.view.WindowManager
                    val isPortrait = windowManager.defaultDisplay.rotation == android.view.Surface.ROTATION_0 ||
                            windowManager.defaultDisplay.rotation == android.view.Surface.ROTATION_180

                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        selectedBarcode?.boundingBox?.let { rect ->
                            val scaleX: Float
                            val scaleY: Float

                            if (isPortrait) {
                                scaleX = size.width / 720f
                                scaleY = size.height / 1280f
                            } else {
                                scaleX = size.width / 1280f
                                scaleY = size.height / 720f
                            }

                            // Aplicamos la escala a las coordenadas del boundingBox
                            val left = rect.left * scaleX
                            val top = rect.top * scaleY
                            val width = rect.width() * scaleX
                            val height = rect.height() * scaleY

                            // Aumentamos ligeramente el tamaño del recuadro para mejor visibilidad
                            val padding = 10f

                            drawRect(
                                color = Color.Green,
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    left - padding,
                                    top - padding
                                ),
                                size = androidx.compose.ui.geometry.Size(
                                    width + padding * 2,
                                    height + padding * 2
                                ),
                                style = Stroke(width = 4f)
                            )
                        }
                    }
                }
            }

            if (isAdvancedMode) {
                selectedBarcode?.let { barcode ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = barcode.value,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    onBarcodeScanned(barcode)
                                    viewModel.clearSelectedBarcode()
                                }
                            ) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            } else {
                // Lista de códigos escaneados (modo simple)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                ) {
                    items(barcodes) { barcode ->
                        Text(
                            text = "Código: ${barcode.value} (formato: ${barcode.format})",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Se necesita permisos de cámara para escanear códigos de barras.",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

// This file is part of the Scanner module, which provides a UI for scanning barcodes using the device camera.
// It is a CameraPreview composable that sets up the camera and an analyzer to process images.
@Composable
private fun CameraPreview(
    viewModel: ScannerViewModel,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val windowManager = remember { context.getSystemService(android.content.Context.WINDOW_SERVICE) as android.view.WindowManager }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val rotation = windowManager.defaultDisplay.rotation

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetRotation(rotation)
                    .setTargetResolution(
                        if (rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_180)
                            Size(720, 1280)  // Vertical
                        else
                            Size(1280, 720)  // Horizontal
                    )
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            viewModel.analyzer
                        )
                    }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            }, ContextCompat.getMainExecutor(context))
        }
    }
}


// Uncomment the following code if you want to display a list of detected barcodes (Hecho arriba de otra forma)
//
//@Composable
//private fun DetectedBarcodesList(barcodes: List<BarcodeData>) {
//    if (barcodes.isNotEmpty()) {
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//                .height(200.dp)
//        ) {
//            items(barcodes) { barcode ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 4.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(text = "Valor: ${barcode}", modifier = Modifier.weight(1f))
//                    Text(text = "Formato: ${barcode.format}",)
//                }
//            }
//        }
//    } else {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(text = "Escanea un codigo de barras para verlo aquí", color = Color.Black)
//        }
//    }
//}
