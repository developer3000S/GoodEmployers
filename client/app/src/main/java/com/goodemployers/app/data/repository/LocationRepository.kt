package com.goodemployers.app.data.repository

import android.content.Context
import android.os.Build
import com.goodemployers.app.data.api.ApiService
import com.goodemployers.app.data.model.AuthRequest
import com.goodemployers.app.data.model.AuthResponse
import com.goodemployers.app.data.model.DeviceInfo
import com.goodemployers.app.data.model.LocationRequest
import com.goodemployers.app.data.model.RegisterRequest
import com.goodemployers.app.util.Constants
import com.goodemployers.app.util.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

interface LocationRepository {
    suspend fun registerClient(name: String): Flow<Resource<AuthResponse>>
    suspend fun loginClient(clientId: String): Flow<Resource<AuthResponse>>
    suspend fun sendLocation(latitude: Double, longitude: Double, accuracy: Float, altitude: Float?, speed: Float?): Flow<Resource<Boolean>>
    suspend fun sendCachedLocations(): Flow<Resource<Boolean>>
    suspend fun getClientDetails(): Flow<Resource<Map<String, Any>>>
}

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val api: ApiService,
    @ApplicationContext private val context: Context
) : LocationRepository {

    private val sharedPreferences = context.getSharedPreferences("goodemployers_prefs", Context.MODE_PRIVATE)

    override suspend fun registerClient(name: String): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        
        try {
            val deviceInfo = DeviceInfo(
                model = Build.MODEL,
                osVersion = Build.VERSION.RELEASE,
                appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
            )
            
            val request = RegisterRequest(name = name, deviceInfo = deviceInfo)
            val response = api.registerClient(request)
            
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    // Save authentication data
                    with(sharedPreferences.edit()) {
                        putString(Constants.PREF_CLIENT_ID, authResponse.accessToken)
                        putString(Constants.PREF_ACCESS_TOKEN, authResponse.accessToken)
                        putString(Constants.PREF_REFRESH_TOKEN, authResponse.refreshToken)
                        putLong(Constants.PREF_TOKEN_EXPIRY, System.currentTimeMillis() + (authResponse.expiresIn * 1000))
                        apply()
                    }
                    
                    emit(Resource.Success(authResponse))
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                emit(Resource.Error("Registration failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unknown error: ${e.message}"))
        }
    }

    override suspend fun loginClient(clientId: String): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        
        try {
            val request = AuthRequest(clientId = clientId)
            val response = api.loginClient(request)
            
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    // Save authentication data
                    with(sharedPreferences.edit()) {
                        putString(Constants.PREF_ACCESS_TOKEN, authResponse.accessToken)
                        putString(Constants.PREF_REFRESH_TOKEN, authResponse.refreshToken)
                        putLong(Constants.PREF_TOKEN_EXPIRY, System.currentTimeMillis() + (authResponse.expiresIn * 1000))
                        apply()
                    }
                    
                    emit(Resource.Success(authResponse))
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                emit(Resource.Error("Login failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unknown error: ${e.message}"))
        }
    }

    override suspend fun sendLocation(
        latitude: Double, 
        longitude: Double, 
        accuracy: Float, 
        altitude: Float?, 
        speed: Float?
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        
        try {
            val accessToken = sharedPreferences.getString(Constants.PREF_ACCESS_TOKEN, null)
                ?: return@flow emit(Resource.Error("Not authenticated"))
            
            val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            
            val locationRequest = LocationRequest(
                latitude = latitude,
                longitude = longitude,
                accuracy = accuracy,
                altitude = altitude,
                speed = speed,
                timestamp = timestamp
            )
            
            val response = api.sendLocation("Bearer $accessToken", locationRequest)
            
            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Failed to send location: ${response.code()} ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unknown error: ${e.message}"))
        }
    }

    override suspend fun sendCachedLocations(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        // Implementation for sending cached locations would go here
        // This would retrieve locations from Room database and send them in a batch
        emit(Resource.Success(true))
    }

    override suspend fun getClientDetails(): Flow<Resource<Map<String, Any>>> = flow {
        emit(Resource.Loading())
        
        try {
            val accessToken = sharedPreferences.getString(Constants.PREF_ACCESS_TOKEN, null)
                ?: return@flow emit(Resource.Error("Not authenticated"))
            
            val clientId = sharedPreferences.getString(Constants.PREF_CLIENT_ID, null)
                ?: return@flow emit(Resource.Error("Client ID not found"))
            
            val response = api.getClientDetails("Bearer $accessToken", clientId)
            
            if (response.isSuccessful) {
                response.body()?.let { details ->
                    emit(Resource.Success(details))
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                emit(Resource.Error("Failed to get client details: ${response.code()} ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unknown error: ${e.message}"))
        }
    }
}
