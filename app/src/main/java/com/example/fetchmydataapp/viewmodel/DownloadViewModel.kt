package com.example.fetchmydataapp.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchmydataapp.model.FileInfo
import com.example.fetchmydataapp.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.documentfile.provider.DocumentFile

class DownloadViewModel : ViewModel(){
    private val apiService = ApiService()

    private val _downloadFolder = MutableStateFlow<Uri?>(null)
    val downloadFolder: StateFlow<Uri?> = _downloadFolder.asStateFlow()

    private val _files = MutableStateFlow<List<FileInfo>>(emptyList())
    val files : StateFlow<List<FileInfo>> = _files.asStateFlow()

    private val _folderName = MutableStateFlow<String>("No folder selected")
    val folderName :StateFlow<String> = _folderName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading : StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _downloadMessage = MutableStateFlow("")
    val downloadMessage: StateFlow<String> = _downloadMessage.asStateFlow()

    fun loadFiles(){
        viewModelScope.launch {
            _isLoading.value = true

            try{
                _files.value = apiService.getFiles("")
            }catch(e: Exception){
                _files.value = emptyList()
            }

            _isLoading.value = false
        }
    }

    fun setDownloadFolder(uri: Uri) {
        _downloadFolder.value = uri
        _folderName.value = uri.lastPathSegment
            ?.substringAfter(":")
            ?:"Unknown Folder"
    }

    fun downloadFile(context: Context,file: FileInfo){
        viewModelScope.launch{
            try{
                val bytes = apiService.downloadFile(file.relativePath)
                val folderUri = _downloadFolder.value ?: return@launch
                val folder = DocumentFile.fromTreeUri(context, folderUri)
                val document = folder?.createFile(
                    "application/octet-stream",
                    file.name
                ) ?: return@launch
                val outputStream = context.contentResolver.openOutputStream(document!!.uri)

                outputStream?.write(bytes)
                outputStream?.close()

                _downloadMessage.value = "Download Successful"
            }catch(e: Exception){
                e.printStackTrace()
                Log.d("DOWNLOAD","Download Failed")
                _downloadMessage.value = e.message ?: "Download Failed"
            }
        }
    }
    fun clearDownloadMessage() {
        _downloadMessage.value = ""
    }
}