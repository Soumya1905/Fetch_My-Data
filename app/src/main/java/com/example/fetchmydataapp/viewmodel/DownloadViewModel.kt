package com.example.fetchmydataapp.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchmydataapp.model.FileInfo
import com.example.fetchmydataapp.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.documentfile.provider.DocumentFile

class DownloadViewModel(application: Application) : AndroidViewModel(application){
    private val apiService = ApiService(application)

    private val _downloadFolder = MutableStateFlow<Uri?>(null)
    val downloadFolder: StateFlow<Uri?> = _downloadFolder.asStateFlow()

    private val _folderName = MutableStateFlow<String>("No folder selected")
    val folderName :StateFlow<String> = _folderName.asStateFlow()

    // relativePath of the file currently downloading, if any. Lets the UI disable
    // and label only that one file's button instead of a single global flag.
    private val _downloadingPath = MutableStateFlow<String?>(null)
    val downloadingPath: StateFlow<String?> = _downloadingPath.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress.asStateFlow()

    private val _downloadMessage = MutableStateFlow("")
    val downloadMessage: StateFlow<String> = _downloadMessage.asStateFlow()

    fun setDownloadFolder(uri: Uri) {
        _downloadFolder.value = uri
        _folderName.value = uri.lastPathSegment
            ?.substringAfter(":")
            ?:"Unknown Folder"
    }

    fun downloadFile(context: Context, file: FileInfo){
        val folderUri = _downloadFolder.value
        if (folderUri == null) {
            _downloadMessage.value = "Choose a download folder first"
            return
        }

        viewModelScope.launch{
            _downloadingPath.value = file.relativePath
            _downloadProgress.value = 0f

            try{
                val bytes = apiService.downloadFile(file.relativePath) { progress ->
                    _downloadProgress.value = progress
                }

                val folder = DocumentFile.fromTreeUri(context, folderUri)
                // createFile lets the SAF provider pick a non-colliding display name
                // (e.g. "report (1).pdf") instead of overwriting an existing file.
                val document = folder?.createFile("application/octet-stream", file.name)
                    ?: throw Exception("Could not create file in the chosen folder")
                val outputStream = context.contentResolver.openOutputStream(document.uri)
                    ?: throw Exception("Could not open the destination file for writing")

                outputStream.use { it.write(bytes) }

                _downloadMessage.value = "Downloaded ${file.name}"
            }catch(e: Exception){
                e.printStackTrace()
                Log.d("DOWNLOAD","Download Failed")
                _downloadMessage.value = e.message ?: "Download Failed"
            } finally {
                _downloadingPath.value = null
                _downloadProgress.value = 0f
            }
        }
    }
    fun clearDownloadMessage() {
        _downloadMessage.value = ""
    }
}
