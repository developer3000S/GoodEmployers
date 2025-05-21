package com.goodemployers.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.goodemployers.app.ui.navigation.Screen
import com.goodemployers.app.ui.viewmodel.AuthState
import com.goodemployers.app.ui.viewmodel.AuthViewModel
import com.goodemployers.app.util.Constants

@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("goodemployers_prefs", 0)
    
    // Check if client ID exists
    val clientId = sharedPreferences.getString(Constants.PREF_CLIENT_ID, null)
    var name by remember { mutableStateOf("") }
    
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            }
            else -> {}
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "GoodEmployers",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (clientId != null) {
            // Login flow
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.loginClient(clientId) },
                modifier = Modifier.fillMaxWidth(0.8f),
                enabled = authState !is AuthState.Loading
            ) {
                Text("Login")
            }
        } else {
            // Registration flow
            Text(
                text = "Please register to continue",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.registerClient(name) },
                modifier = Modifier.fillMaxWidth(0.8f),
                enabled = name.isNotBlank() && authState !is AuthState.Loading
            ) {
                Text("Register")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (authState) {
            is AuthState.Loading -> {
                CircularProgressIndicator()
            }
            is AuthState.Error -> {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            else -> {}
        }
    }
}
