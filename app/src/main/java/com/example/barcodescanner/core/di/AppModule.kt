package com.example.barcodescanner.core.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    //A añadir inyec. dependencias aquí si es necesario (repositorio, base de datos para guardar barcodes, etc.)
}