package com.goodemployers.app.util

object Constants {
    // Update the BASE_URL to point to the deployed server
    const val BASE_URL = "https://8000-ia6lw3bxw9bcimqwscreo-6fe44999.manusvm.computer/"
    const val LOCATION_UPDATE_INTERVAL = 60000L // 60 seconds in milliseconds
    const val LOCATION_FASTEST_INTERVAL = 30000L // 30 seconds
    const val LOCATION_DISPLACEMENT = 10f // 10 meters
    
    const val PREF_CLIENT_ID = "client_id"
    const val PREF_ACCESS_TOKEN = "access_token"
    const val PREF_REFRESH_TOKEN = "refresh_token"
    const val PREF_TOKEN_EXPIRY = "token_expiry"
    
    const val NOTIFICATION_CHANNEL_ID = "location_service_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Location Service"
    const val NOTIFICATION_ID = 1001
}
