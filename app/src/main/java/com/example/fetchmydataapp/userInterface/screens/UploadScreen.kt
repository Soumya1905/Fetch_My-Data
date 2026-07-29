package com.example.fetchmydataapp.userInterface.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fetchmydataapp.network.ApiService
import com.example.fetchmydataapp.viewmodel.UploadViewModel

@Composable
fun UploadScreen(
        navController: NavController
){
    val uploadViewModel: UploadViewModel = viewModel()
    val context = LocalContext.current

    val isUploading by uploadViewModel.isUploaing.collectAsState()
    val uploadMessage by uploadViewModel.uploadMessage.collectAsState()

    var selectedFile by remember{
        mutableStateOf<Uri?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ){
        uri-> selectedFile = uri
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text("Selected File:", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            selectedFile?.let{ ApiService().getFileName(LocalContext.current,it)}
                ?:"No File Selected"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                launcher.launch("*/*")
            }
        ){
            Text("Choose File")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = selectedFile != null && !isUploading,
            onClick = {
                selectedFile?.let{
                    uploadViewModel.uploadFile(context,it)
                }
            }
        ){
            Text(
                if(isUploading){
                    "Uploading..."
                } else {
                    "Upload"
                }
            )
        }

        if (uploadMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(uploadMessage)
        }
    }
}