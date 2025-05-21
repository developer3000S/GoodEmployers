package com.goodemployers.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.goodemployers.app.ui.viewmodel.ClientDetailsState
import com.goodemployers.app.ui.viewmodel.LocationState
import com.goodemployers.app.ui.viewmodel.LocationViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: LocationViewModel = hiltViewModel()
) {
    val locationState by viewModel.locationState.collectAsState()
    val clientDetailsState by viewModel.clientDetailsState.collectAsState()
    
    // Fetch client details on screen load
    LaunchedEffect(Unit) {
        viewModel.getClientDetails()
    }
    
    // Periodically check for cached locations to send
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.sendCachedLocations()
            delay(300000) // Check every 5 minutes
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "GoodEmployers",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Client details section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Client Information",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                when (clientDetailsState) {
                    is ClientDetailsState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    is ClientDetailsState.Success -> {
                        val details = (clientDetailsState as ClientDetailsState.Success).details
                        
                        Text("Name: ${details["name"] ?: "Unknown"}")
                        Text("Client ID: ${details["id"] ?: "Unknown"}")
                        Text("Last Active: ${formatDate(details["last_active"] as? String)}")
                        Text("Location Count: ${details["location_count"] ?: "0"}")
                    }
                    is ClientDetailsState.Error -> {
                        Text(
                            text = (clientDetailsState as ClientDetailsState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {
                        Text("Loading client details...")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Location status section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Location Tracking Status",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                when (locationState) {
                    is LocationState.Sending -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Text("Sending location data...")
                        }
                    }
                    is LocationState.Success -> {
                        Text(
                            text = "Location data sent successfully",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is LocationState.Error -> {
                        Text(
                            text = "Error: ${(locationState as LocationState.Error).message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {
                        Text("Location service is running in the background")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "GPS updates are configured to send every 60 seconds",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Manual location update button
        Button(
            onClick = { viewModel.sendCachedLocations() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Cached Locations")
        }
    }
}

private fun formatDate(dateString: String?): String {
    if (dateString == null) return "Unknown"
    
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)
        
        val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()
        outputFormat.format(date)
    } catch (e: Exception) {
        dateString
    }
}
