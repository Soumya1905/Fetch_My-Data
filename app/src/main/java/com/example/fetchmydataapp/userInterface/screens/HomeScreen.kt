package com.example.fetchmydataapp.userInterface.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fetchmydataapp.userInterface.components.ActionCard
import com.example.fetchmydataapp.userInterface.navigation.Routes
import com.example.fetchmydataapp.viewmodel.FileViewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

@Composable
fun HomeScreen(navController: NavController){
    val viewModel: FileViewModel = viewModel()
    val isConnected by viewModel.isConnected.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkConnection()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Connected Device",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = "Laptop-name-here",
                    fontSize = 20.sp
                )
                Text("IP-address-here",
                    fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        ActionCard("Download", onClick = {
            navController.navigate(Routes.DOWNLOAD)
        })

        Spacer(modifier = Modifier.height(12.dp))

        ActionCard("Upload", onClick = {
            navController.navigate(Routes.UPLOAD)
        })

        Spacer(modifier = Modifier.height(12.dp))

        ActionCard(
            "View Files", onClick = {
                navController.navigate(Routes.VIEWFILES)
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            if(isConnected) {
                Text("\uD83D\uDFE2 Connected")
            } else {
                Text("\uD83D\uDD34 Disconnected")
            }
        }
    }
}