@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fetchmydataapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fetchmydataapp.ui.theme.FetchMyDataAppTheme
import com.example.fetchmydataapp.userInterface.navigation.Routes
import com.example.fetchmydataapp.userInterface.screens.DownloadScreen
import com.example.fetchmydataapp.userInterface.screens.HomeScreen
import com.example.fetchmydataapp.userInterface.screens.UploadScreen
import com.example.fetchmydataapp.userInterface.screens.ViewFilePage


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FetchMyDataAppTheme {

                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                         CenterAlignedTopAppBar(
                            title = {
                                Text("Fetch My Data")
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        navController.popBackStack()
                                    }
                                ) {
                                    Text(
                                        "\uD81A\uDC3F",
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Routes.HOME,
                        modifier = Modifier.padding(innerPadding)
                    ){
                        composable(Routes.HOME){
                            HomeScreen(navController)
                        }

                        composable(Routes.VIEWFILES){
                            ViewFilePage(navController)
                        }

                        composable(
                            route = Routes.UPLOAD,
                            arguments = listOf(navArgument("path") { type = NavType.StringType; defaultValue = "" })
                        ) { backStackEntry ->
                            val path = backStackEntry.arguments?.getString("path") ?: ""
                            UploadScreen(navController, path)
                        }

                        composable(Routes.DOWNLOAD){
                            DownloadScreen(navController)
                        }
                    }
                }
            }
        }
    }
}