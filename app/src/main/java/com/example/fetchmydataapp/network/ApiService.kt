package com.example.fetchmydataapp.network

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.fetchmydataapp.model.FileInfo
import io.ktor.client.call.*
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.coroutines.flow.first

class ApiService(private val context: Context) {

    private suspend fun requirePairing(): PairingInfo {
        return PairingStore.pairingFlow(context).first()
            ?: throw IllegalStateException("Not paired with a laptop yet. Scan the QR code to pair.")
    }

    suspend fun ping(): String {
        val pairing = requirePairing()
        return KtorClient.client
            .get("http://${pairing.host}:${pairing.port}/ping")
            .body()
    }

    suspend fun getFiles(path: String): List<FileInfo> {
        val pairing = requirePairing()
        return KtorClient.client
            .get("http://${pairing.host}:${pairing.port}/files") {
                header(HttpHeaders.Authorization, "Bearer ${pairing.token}")
                parameter("path", path)
            }
            .body()
    }

    suspend fun downloadFile(path: String, onProgress: (Float) -> Unit = {}): ByteArray {
        val pairing = requirePairing()
        return KtorClient.client
            .get("http://${pairing.host}:${pairing.port}/download") {
                header(HttpHeaders.Authorization, "Bearer ${pairing.token}")
                parameter("path", path)
                onDownload { bytesSentTotal, contentLength ->
                    if (contentLength != null && contentLength > 0) {
                        onProgress(bytesSentTotal.toFloat() / contentLength)
                    }
                }
            }
            .body()
    }

    suspend fun uploadFile(uri: Uri, path: String = ""): String {
        val pairing = requirePairing()
        val inputStream = context.contentResolver.openInputStream(uri) ?: throw Exception("Unable to open file")
        val fileName = getFileName(uri)

        return KtorClient.client.post("http://${pairing.host}:${pairing.port}/upload") {
            header(HttpHeaders.Authorization, "Bearer ${pairing.token}")
            parameter("path", path)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            inputStream.readBytes(),
                            Headers.build {
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

    fun getFileName(uri: Uri): String {
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
