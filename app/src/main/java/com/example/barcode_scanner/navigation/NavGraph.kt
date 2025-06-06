package com.example.barcode_scanner.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.barcode_scanner.ui.home.HomeScreen
import com.example.scanner.ui.ScannerScreen

object Routes {
    const val HOME = "home"
    const val SCANNER = "scanner"
}

@Composable
fun NavGraph (navController: NavHostController){
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onNavigateToScanner = {
                navController.navigate(Routes.SCANNER)
            })
        }
        composable(Routes.SCANNER) {
            ScannerScreen(onBack = {
                navController.popBackStack()
            })
        }
    }
}
