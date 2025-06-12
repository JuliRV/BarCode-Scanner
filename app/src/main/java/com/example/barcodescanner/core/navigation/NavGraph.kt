package com.example.barcodescanner.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.barcodescanner.core.ui.home.HomeScreen
import com.example.barcodescanner.features.barcodehistory.presentation.history.BarcodeHistoryScreen
import com.example.scanner.ui.ScannerScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.barcodescanner.features.barcodehistory.presentation.scanner.AppScannerViewModel
import androidx.compose.runtime.LaunchedEffect
import com.example.scanner.ui.ScannerViewModel

object Routes {
    const val HOME = "home"
    const val SCANNER = "scanner"
    const val HISTORY = "history"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToScanner = {
                    navController.navigate(Routes.SCANNER)
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.HISTORY)
                }
            )
        }
        composable(Routes.SCANNER) {
            val scannerViewModel: ScannerViewModel = hiltViewModel()
            val appScannerViewModel: AppScannerViewModel = hiltViewModel()

            // Conectar los ViewModels
            LaunchedEffect(scannerViewModel) {
                appScannerViewModel.collectBarcodes(scannerViewModel.barcodeFlow)
            }

            ScannerScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.HISTORY) {
            BarcodeHistoryScreen()
        }
    }
}
