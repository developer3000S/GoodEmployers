package com.goodemployers.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodemployers.app.data.repository.LocationRepository
import com.goodemployers.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {

    private val _locationState = MutableStateFlow<LocationState>(LocationState.Idle)
    val locationState: StateFlow<LocationState> = _locationState

    private val _clientDetailsState = MutableStateFlow<ClientDetailsState>(ClientDetailsState.Idle)
    val clientDetailsState: StateFlow<ClientDetailsState> = _clientDetailsState

    fun sendLocation(latitude: Double, longitude: Double, accuracy: Float, altitude: Float? = null, speed: Float? = null) {
        viewModelScope.launch {
            repository.sendLocation(latitude, longitude, accuracy, altitude, speed).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _locationState.value = LocationState.Sending
                    }
                    is Resource.Success -> {
                        _locationState.value = LocationState.Success
                    }
                    is Resource.Error -> {
                        _locationState.value = LocationState.Error(result.message ?: "Unknown error")
                    }
                }
            }.launchIn(this)
        }
    }

    fun sendCachedLocations() {
        viewModelScope.launch {
            repository.sendCachedLocations().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _locationState.value = LocationState.Sending
                    }
                    is Resource.Success -> {
                        _locationState.value = LocationState.Success
                    }
                    is Resource.Error -> {
                        _locationState.value = LocationState.Error(result.message ?: "Unknown error")
                    }
                }
            }.launchIn(this)
        }
    }

    fun getClientDetails() {
        viewModelScope.launch {
            repository.getClientDetails().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _clientDetailsState.value = ClientDetailsState.Loading
                    }
                    is Resource.Success -> {
                        result.data?.let { details ->
                            _clientDetailsState.value = ClientDetailsState.Success(details)
                        }
                    }
                    is Resource.Error -> {
                        _clientDetailsState.value = ClientDetailsState.Error(result.message ?: "Unknown error")
                    }
                }
            }.launchIn(this)
        }
    }
}

sealed class LocationState {
    object Idle : LocationState()
    object Sending : LocationState()
    object Success : LocationState()
    data class Error(val message: String) : LocationState()
}

sealed class ClientDetailsState {
    object Idle : ClientDetailsState()
    object Loading : ClientDetailsState()
    data class Success(val details: Map<String, Any>) : ClientDetailsState()
    data class Error(val message: String) : ClientDetailsState()
}
