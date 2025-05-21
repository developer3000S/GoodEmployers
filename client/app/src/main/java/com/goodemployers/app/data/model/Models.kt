package com.goodemployers.app.data.model

import com.google.gson.annotations.SerializedName

data class DeviceInfo(
    val model: String,
    @SerializedName("os_version") val osVersion: String,
    @SerializedName("app_version") val appVersion: String
)

data class RegisterRequest(
    val name: String,
    @SerializedName("device_info") val deviceInfo: DeviceInfo
)

data class AuthRequest(
    @SerializedName("client_id") val clientId: String
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("token_type") val tokenType: String
)

data class LocationRequest(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val altitude: Float? = null,
    val speed: Float? = null,
    val timestamp: String
)

data class LocationResponse(
    val id: Long,
    @SerializedName("received_at") val receivedAt: String
)

data class ClientDetails(
    val id: String,
    val name: String,
    @SerializedName("device_info") val deviceInfo: DeviceInfo,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("last_active") val lastActive: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("location_count") val locationCount: Int
)
