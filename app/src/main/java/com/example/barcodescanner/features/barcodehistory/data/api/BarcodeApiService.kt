package com.example.barcodescanner.features.barcodehistory.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Servicio de API para sincronizar códigos de barras con el servidor.
 * 
 * Nota: Esta es una interfaz mock preparada para futura integración con un backend real.
 * Por ahora, se puede usar con un servidor mock o interceptor para testing.
 */
interface BarcodeApiService {

    /**
     * Sincroniza una lista de códigos de barras con el servidor
     * 
     * @param request Objeto con la lista de códigos a sincronizar
     * @return Response con el resultado de la sincronización
     */
    @POST("api/barcodes/sync")
    suspend fun syncBarcodes(@Body request: SyncBarcodesRequest): Response<SyncBarcodesResponse>
}

/**
 * Clase para la solicitud de sincronización
 */
data class SyncBarcodesRequest(
    val deviceId: String,
    val timestamp: Long,
    val barcodes: List<BarcodeDto>
)

/**
 * DTO para representar un código de barras en la API
 */
data class BarcodeDto(
    val id: Int,
    val code: String,
    val timestamp: Long
)

/**
 * Clase para la respuesta de sincronización
 */
data class SyncBarcodesResponse(
    val success: Boolean,
    val message: String,
    val syncedCount: Int,
    val timestamp: Long
)

