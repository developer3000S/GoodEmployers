package com.goodemployers.app.data.api

import com.goodemployers.app.data.model.AuthRequest
import com.goodemployers.app.data.model.AuthResponse
import com.goodemployers.app.data.model.LocationRequest
import com.goodemployers.app.data.model.LocationResponse
import com.goodemployers.app.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/register")
    suspend fun registerClient(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("api/auth/login")
    suspend fun loginClient(@Body request: AuthRequest): Response<AuthResponse>
    
    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: Map<String, String>): Response<AuthResponse>
    
    @POST("api/locations")
    suspend fun sendLocation(
        @Header("Authorization") token: String,
        @Body location: LocationRequest
    ): Response<LocationResponse>
    
    @POST("api/locations/batch")
    suspend fun sendLocations(
        @Header("Authorization") token: String,
        @Body locations: Map<String, List<LocationRequest>>
    ): Response<Map<String, Any>>
    
    @GET("api/clients/{clientId}")
    suspend fun getClientDetails(
        @Header("Authorization") token: String,
        @Path("clientId") clientId: String
    ): Response<Map<String, Any>>
}
