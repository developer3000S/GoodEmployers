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
class AuthViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun registerClient(name: String) {
        viewModelScope.launch {
            repository.registerClient(name).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _authState.value = AuthState.Loading
                    }
                    is Resource.Success -> {
                        result.data?.let { authResponse ->
                            _authState.value = AuthState.Authenticated(authResponse.accessToken)
                        }
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState.Error(result.message ?: "Unknown error")
                    }
                }
            }.launchIn(this)
        }
    }

    fun loginClient(clientId: String) {
        viewModelScope.launch {
            repository.loginClient(clientId).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _authState.value = AuthState.Loading
                    }
                    is Resource.Success -> {
                        result.data?.let { authResponse ->
                            _authState.value = AuthState.Authenticated(authResponse.accessToken)
                        }
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState.Error(result.message ?: "Unknown error")
                    }
                }
            }.launchIn(this)
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
