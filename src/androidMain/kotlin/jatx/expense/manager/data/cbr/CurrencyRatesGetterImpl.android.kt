package jatx.expense.manager.data.cbr

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout

actual fun httpClient() = HttpClient(Android) {
    install(HttpTimeout) {
        // Time in milliseconds
        requestTimeoutMillis = 12000 // Total time for the request
        connectTimeoutMillis = 4000  // Time to establish a connection
        socketTimeoutMillis = 4000   // Max time between two data packets
    }
}