package com.example.fetchmydataapp.userInterface.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fetchmydataapp.model.FileInfo

@Composable
fun FileCard(
    file: FileInfo,
    onClick:()-> Unit
){
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = when(file.extension){
                    "Folder" -> "📁"
                    "ZIP Archive" -> "📦"
                    else -> "📄"
                }
            )
            Column {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = file.extension,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}