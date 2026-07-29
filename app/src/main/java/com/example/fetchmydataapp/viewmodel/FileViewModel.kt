package com.example.fetchmydataapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchmydataapp.model.FileInfo
import com.example.fetchmydataapp.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FileViewModel: ViewModel(){
    private val apiService = ApiService()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _files = MutableStateFlow<List<FileInfo>>(emptyList())
    val files: StateFlow<List<FileInfo>> = _files.asStateFlow()

    private val _currentPath = MutableStateFlow("")
    val currentPath: StateFlow<String> = _currentPath.asStateFlow()

    private val pathStack = mutableListOf("")



    fun checkConnection(){
        viewModelScope.launch{
            try{
                val response = apiService.ping()
                _isConnected.value = response == "pong"
            } catch (e:Exception){
                _isConnected.value = false
            }
        }
    }

    fun openFolder(path: String){
        pathStack.add(path)
        loadFiles(path)
    }

    fun goBackFolder(): Boolean{
        if(pathStack.size <= 1){
            return false
        }
        pathStack.removeLast()

        loadFiles(pathStack.last())

        return true
    }

    fun loadFiles(path:String = ""){
        viewModelScope.launch{
            try{
                _currentPath.value = path
                _files.value = apiService.getFiles(path)
            } catch (e:Exception){
                _isConnected.value = false
            }
        }
    }
}