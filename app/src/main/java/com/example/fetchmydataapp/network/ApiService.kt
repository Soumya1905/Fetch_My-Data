package com.example.fetchmydataapp.network

import com.example.fetchmydataapp.model.FileInfo
import com.example.fetchmydataapp.network.networkConstrants.SERVER_IP
import com.example.fetchmydataapp.network.networkConstrants.SERVER_PORT
import io.ktor.client.call.*
import io.ktor.client.request.*
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import io.ktor.client.request.forms.*
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*

class ApiService {
    private val baseUrl = "http://$SERVER_IP:$SERVER_PORT"

    suspend fun ping(): String {
        return KtorClient.client
            .get("$baseUrl/ping")
            .body()
    }
    suspend fun getFiles(path: String): List<FileInfo> {
        return KtorClient.client
            .get("$baseUrl/files?path=$path")
            .body()
    }

    suspend fun downloadFile(path: String): ByteArray{
        return KtorClient.client
            .get("$baseUrl/download"){
                parameter("path",path)
            }
            .body()
    }

    suspend fun uploadFile(context: Context,uri: Uri): String{
        val inputStream = context.contentResolver.openInputStream(uri)?: throw Exception("Unable to open file")

        val fileName = getFileName(context,uri)

        return KtorClient.client.post("$baseUrl/upload"){
            setBody(
                MultiPartFormDataContent(
                    formData{
                        append(
                            "file",
                            inputStream.readBytes(),
                            Headers.build{
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"$fileName\""
                                )
                            }
                        )
                    }
                )
            )
        }.bodyAsText()
    }

    fun getFileName(context: Context, uri: Uri): String {
        var fileName = "uploadFile"

        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                fileName = cursor.getString(nameIndex)
            }
        }

        return fileName
    }
}