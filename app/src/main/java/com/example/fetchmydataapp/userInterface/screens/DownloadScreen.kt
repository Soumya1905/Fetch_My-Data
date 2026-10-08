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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.fetchmydataapp.viewmodel.FileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(navController: NavController){
    val fileViewModel: FileViewModel = viewModel()
    val downloadViewModel: DownloadViewModel = viewModel()

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    val files by fileViewModel.files.collectAsState()
    val currentPath by fileViewModel.currentPath.collectAsState()
    val downloadFolder by downloadViewModel.downloadFolder.collectAsState()
    val folderName by downloadViewModel.folderName.collectAsState()
    val downloadingPath by downloadViewModel.downloadingPath.collectAsState()
    val downloadProgress by downloadViewModel.downloadProgress.collectAsState()
    val downloadMessage by downloadViewModel.downloadMessage.collectAsState()

    // Load the laptop's files as soon as the screen opens, independent of
    // whether a local save folder has been picked yet.
    LaunchedEffect(Unit) {
        fileViewModel.refresh()
    }

    LaunchedEffect(downloadMessage) {
        if (downloadMessage.isNotBlank()) {
            snackbarHostState.showSnackbar(message = downloadMessage)
            downloadViewModel.clearDownloadMessage()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) {
        uri->
        if(uri != null){
            downloadViewModel.setDownloadFolder(uri)
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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            CenterAlignedTopAppBar(
                title = { Text("Download") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            val wentBack = fileViewModel.goBackFolder()

                            if (!wentBack) {
                                navController.popBackStack()
                            }
                        }
                    ){
                        Icon(imageVector = Icons.Default.Home, contentDescription = "Back")
                    }
                }
            )

            Text(
                "Folder: /$currentPath",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    launcher.launch(null)
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("Choose Download Folder")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Save to: $folderName",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (files.isEmpty()) {
                Text(
                    "No files present",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(files) { file ->
                        FileCard(
                            file = file,
                            onClick = {
                                if (file.isDirectory) {
                                    fileViewModel.openFolder(file.relativePath)
                                }
                            },
                            trailingContent = {
                                if (!file.isDirectory) {
                                    val isThisFileDownloading = downloadingPath == file.relativePath
                                    if (isThisFileDownloading) {
                                        Text("Downloading ${(downloadProgress * 100).toInt()}%")
                                    } else {
                                        Button(
                                            enabled = downloadingPath == null && downloadFolder != null,
                                            onClick = {
                                                downloadViewModel.downloadFile(context, file)
                                            }
                                        ) {
                                            Text("Download")
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
