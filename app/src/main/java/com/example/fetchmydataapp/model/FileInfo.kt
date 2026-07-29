package com.example.fetchmydataapp.model

import kotlinx.serialization.Serializable

@Serializable
data class FileInfo(
    val name: String,
    val size: Long,
    val isDirectory: Boolean,
    val extension: String,
    val relativePath: String
)