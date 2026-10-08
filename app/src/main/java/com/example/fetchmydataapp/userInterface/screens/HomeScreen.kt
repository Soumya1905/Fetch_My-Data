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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fetchmydataapp.userInterface.components.ActionCard
import com.example.fetchmydataapp.userInterface.navigation.Routes
import com.example.fetchmydataapp.viewmodel.FileViewModel
import com.example.fetchmydataapp.viewmodel.PairingViewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun HomeScreen(navController: NavController){
    val viewModel: FileViewModel = viewModel()
    val pairingViewModel: PairingViewModel = viewModel()
    val isConnected by viewModel.isConnected.collectAsState()
    val pairing by pairingViewModel.pairing.collectAsState()
    val pairError by pairingViewModel.pairError.collectAsState()

    val context = LocalContext.current

    val scannerOptions = remember {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    }

    fun launchScan() {
        GmsBarcodeScanning.getClient(context, scannerOptions)
            .startScan()
            .addOnSuccessListener { barcode ->
                barcode.rawValue?.let { pairingViewModel.pairFromQr(it) }
            }
    }

    LaunchedEffect(Unit) {
        viewModel.checkConnection()
    }

    LaunchedEffect(pairing) {
        if (pairing != null) {
            viewModel.checkConnection()
        }
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
                    text = pairing?.host ?: "Not paired yet",
                    fontSize = 20.sp
                )
                pairing?.let {
                    Text("Port: ${it.port}", fontSize = 20.sp)
                }
                pairError?.let {
                    Text(it, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = { launchScan() }) {
                    Text(if (pairing == null) "Pair with Laptop" else "Re-pair")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        ActionCard("Download", onClick = {
            navController.navigate(Routes.DOWNLOAD)
        })

        Spacer(modifier = Modifier.height(12.dp))

        ActionCard("Upload", onClick = {
            navController.navigate(Routes.upload())
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
                Text("🟢 Connected")
            } else {
                Text("🔴 Disconnected")
            }
        }
    }
}
