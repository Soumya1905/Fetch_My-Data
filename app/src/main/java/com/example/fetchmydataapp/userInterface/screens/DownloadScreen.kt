package com.example.fetchmydataapp.userInterface.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fetchmydataapp.userInterface.components.FileCard
import com.example.fetchmydataapp.viewmodel.DownloadViewModel

@Composable
fun DownloadScreen(navController: NavController){
    val viewModel: DownloadViewModel = viewModel()

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    val files by viewModel.files.collectAsState()
    val downloadFolder by viewModel.downloadFolder.collectAsState()
    val folderName by viewModel.folderName.collectAsState()
    val downloadMessage by viewModel.downloadMessage.collectAsState()
    LaunchedEffect(downloadMessage) {

        if (downloadMessage.isNotBlank()) {

            snackbarHostState.showSnackbar(
                message = downloadMessage
            )

            viewModel.clearDownloadMessage()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) {
        uri->
        if(uri != null){
            viewModel.setDownloadFolder(uri)

            viewModel.loadFiles()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { padding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Button(
                onClick = {
                    launcher.launch(null)
                }
            ) {
                Text("Choose Download Folder")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Folder: $folderName"
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(files) { file ->
                    FileCard(
                        file = file,
                        onClick = {
                            if(!file.isDirectory){
                                viewModel.downloadFile(
                                    context = context,
                                    file
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}