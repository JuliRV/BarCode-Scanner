package com.example.barcodescanner.features.barcodehistory.di

import android.content.Context
import androidx.room.Room
import com.example.barcodescanner.features.barcodehistory.data.api.BarcodeApiService
import com.example.barcodescanner.features.barcodehistory.data.dao.BarcodeDao
import com.example.barcodescanner.features.barcodehistory.data.database.BarcodeDatabase
import com.example.barcodescanner.features.barcodehistory.data.repository.BarcodeRepositoryImpl
import com.example.barcodescanner.features.barcodehistory.domain.repository.BarcodeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
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
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideBarcodeDao(database: BarcodeDatabase): BarcodeDao = database.barcodeDao()

    @Provides
    @Singleton
    fun provideBarcodeRepository(
        barcodeDao: BarcodeDao
    ): BarcodeRepository = BarcodeRepositoryImpl(barcodeDao)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // URL base mock - en producción esto vendría de BuildConfig o similar
        val baseUrl = "https://api.example.com/"
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBarcodeApiService(retrofit: Retrofit): BarcodeApiService {
        return retrofit.create(BarcodeApiService::class.java)
    }
}
