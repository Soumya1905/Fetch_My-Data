package com.example.fetchmydataapp.userInterface.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fetchmydataapp.userInterface.components.FileCard
import com.example.fetchmydataapp.viewmodel.FileViewModel
import androidx.compose.material.icons.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewFilePage(
    navController: NavController
) {
    val viewModel: FileViewModel = viewModel()

    val files by viewModel.files.collectAsState() //makes List<FileInfo>

    LaunchedEffect(Unit) {
        viewModel.loadFiles()
    }


    var searchText by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        CenterAlignedTopAppBar(
            title = {
                Text("Files")
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        val wentBack = viewModel.goBackFolder()

                        if (!wentBack) {
                            navController.popBackStack()
                        }
                    }
                ){
                    Icon(imageVector = Icons.Default.ArrowBack,contentDescription = "Back")
                }
            }
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            label = {
                Text("Search Files")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        if(files.isEmpty()){
            Text(
                "No Files Present",
                modifier = Modifier.padding(16.dp)
            )
        }
        else{
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {

                itemsIndexed(files) { _, file ->
                    FileCard(
                        file = file,
                        onClick = {
                            if(file.isDirectory){
                                viewModel.openFolder(file.relativePath)
                            }
                        }
                    )
                }
            }
        }
    }
}