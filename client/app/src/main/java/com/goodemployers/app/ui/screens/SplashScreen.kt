package com.goodemployers.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.goodemployers.app.ui.navigation.Screen
import com.goodemployers.app.util.Constants
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("goodemployers_prefs", 0)
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(key1 = true) {
        delay(1500) // Show splash for 1.5 seconds
        isLoading = false
        
        // Check if user is already authenticated
        val clientId = sharedPreferences.getString(Constants.PREF_CLIENT_ID, null)
        val accessToken = sharedPreferences.getString(Constants.PREF_ACCESS_TOKEN, null)
        val tokenExpiry = sharedPreferences.getLong(Constants.PREF_TOKEN_EXPIRY, 0)
        
        val destination = if (clientId != null && accessToken != null && tokenExpiry > System.currentTimeMillis()) {
            // User is authenticated and token is valid
            Screen.Home.route
        } else {
            // User needs to authenticate
            Screen.Auth.route
        }
        
        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}
