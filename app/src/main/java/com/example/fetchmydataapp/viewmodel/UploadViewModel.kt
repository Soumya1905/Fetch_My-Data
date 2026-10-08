package com.example.fetchmydataapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchmydataapp.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class UploadViewModel(application: Application) : AndroidViewModel(application){
    private val apiService = ApiService(application)

    private val _isUploading = MutableStateFlow(false)
    val isUploaing: StateFlow<Boolean> = _isUploading.asStateFlow()

    private val _uploadMessage = MutableStateFlow("")
    val uploadMessage: StateFlow<String> = _uploadMessage

    fun getFileNameFor(uri: Uri): String = apiService.getFileName(uri)

    fun uploadFile(uri: Uri, path: String = ""){
        viewModelScope.launch {
            _isUploading.value = true

            try{
                val response = apiService.uploadFile(uri, path)
                _uploadMessage.value = response
            } catch(e: Exception){
                _uploadMessage.value = e.message ?: "Upload Failed"
            }

            _isUploading.value = false
        }
    }
}
