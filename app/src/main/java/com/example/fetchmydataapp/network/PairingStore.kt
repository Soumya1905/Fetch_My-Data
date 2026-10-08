package com.example.fetchmydataapp.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val Context.pairingDataStore by preferencesDataStore(name = "pairing")

@Serializable
data class PairingInfo(
    val host: String,
    val port: Int,
    val token: String
)

object PairingStore {
    private val HOST = stringPreferencesKey("host")
    private val PORT = intPreferencesKey("port")
    private val TOKEN = stringPreferencesKey("token")

    fun pairingFlow(context: Context): Flow<PairingInfo?> {
        return context.pairingDataStore.data.map { prefs ->
            val host = prefs[HOST]
            val port = prefs[PORT]
            val token = prefs[TOKEN]

            if (host != null && port != null && token != null) {
                PairingInfo(host, port, token)
            } else {
                null
            }
        }
    }

    suspend fun save(context: Context, info: PairingInfo) {
        context.pairingDataStore.edit { prefs ->
            prefs[HOST] = info.host
            prefs[PORT] = info.port
            prefs[TOKEN] = info.token
        }
    }

    suspend fun parseAndSave(context: Context, qrPayload: String): Boolean {
        return try {
            val info = Json.decodeFromString<PairingInfo>(qrPayload)
            save(context, info)
            true
        } catch (e: Exception) {
            false
        }
    }
}
