package com.example.fetchmydataapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchmydataapp.network.PairingInfo
import com.example.fetchmydataapp.network.PairingStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PairingViewModel(application: Application) : AndroidViewModel(application) {

    val pairing: StateFlow<PairingInfo?> = PairingStore.pairingFlow(application)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _pairError = MutableStateFlow<String?>(null)
    val pairError: StateFlow<String?> = _pairError.asStateFlow()

    fun pairFromQr(payload: String) {
        viewModelScope.launch {
            val success = PairingStore.parseAndSave(getApplication(), payload)
            _pairError.value = if (success) null else "That QR code isn't a valid pairing code"
        }
    }
}
