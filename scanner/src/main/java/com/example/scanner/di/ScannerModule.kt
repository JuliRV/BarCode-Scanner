package com.example.scanner.di

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import com.example.scanner.data.analyzer.BarcodeAnalyzer
import com.example.scanner.data.repository.BarcodeScannerRepositoryImpl
import com.example.scanner.domain.repository.BarcodeScannerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScannerModule {
    @Binds
    @Singleton
    abstract fun provideBarcodeScannerRepository(
        impl: BarcodeScannerRepositoryImpl
    ): BarcodeScannerRepository

    @Binds
    @Singleton
    abstract fun provideImageAnalyzer(
        analyzer: BarcodeAnalyzer
    ): ImageAnalysis.Analyzer
}