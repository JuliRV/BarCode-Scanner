package com.example.barcodescanner.di

import android.content.Context
import androidx.room.Room
import com.example.barcodescanner.data.dao.BarcodeDao
import com.example.barcodescanner.data.database.BarcodeDatabase
import com.example.barcodescanner.data.repository.BarcodeRepository
import com.example.barcodescanner.data.repository.BarcodeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideBarcodeDatabase(
        @ApplicationContext context: Context
    ): BarcodeDatabase = Room.databaseBuilder(
        context,
        BarcodeDatabase::class.java,
        "barcode_database"
    ).build()

    @Provides
    fun provideBarcodeDao(database: BarcodeDatabase): BarcodeDao = database.barcodeDao()

    @Provides
    @Singleton
    fun provideBarcodeRepository(
        barcodeDao: BarcodeDao
    ): BarcodeRepository = BarcodeRepositoryImpl(barcodeDao)
}
