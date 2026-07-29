package com.example.fetchmydataapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchmydataapp.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class UploadViewModel : ViewModel(){
    private val apiService = ApiService()

    private val _isUploading = MutableStateFlow(false)
    val isUploaing: StateFlow<Boolean> = _isUploading.asStateFlow()

    private val _uploadMessage = MutableStateFlow("")
    val uploadMessage: StateFlow<String> = _uploadMessage

    fun uploadFile(context: Context,uri: Uri){
        viewModelScope.launch {
            _isUploading.value = true

            try{
                val response = apiService.uploadFile(context,uri)
                _uploadMessage.value = response
            } catch(e: Exception){
                _uploadMessage.value = e.message ?: "Upload Failed"
            }

            _isUploading.value = false
        }
    }
}