package com.goodemployers.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.goodemployers.app.ui.screens.AuthScreen
import com.goodemployers.app.ui.screens.HomeScreen
import com.goodemployers.app.ui.screens.SplashScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        
        composable(Screen.Auth.route) {
            AuthScreen(navController = navController)
        }
        
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object Home : Screen("home")
}
