package com.example.fetchmydataapp.userInterface.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fetchmydataapp.userInterface.components.FileCard
import com.example.fetchmydataapp.userInterface.navigation.Routes
import com.example.fetchmydataapp.viewmodel.FileViewModel
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewFilePage(
    navController: NavController
) {
    val viewModel: FileViewModel = viewModel()

    val files by viewModel.files.collectAsState() //makes List<FileInfo>
    val currentPath by viewModel.currentPath.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Folder: /" + currentPath,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                navController.navigate(Routes.upload(currentPath))
            }) {
                Text("Upload here")
            }
        }

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
