package com.example.fetchmydataapp.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

object KtorClient{
    val client = HttpClient(OkHttp){
        install(ContentNegotiation){
            json()
        }
    }
}