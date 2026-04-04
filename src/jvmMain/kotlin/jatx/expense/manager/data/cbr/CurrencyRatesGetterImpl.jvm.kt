package jatx.expense.manager.data.cbr

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java

actual fun httpClient() = HttpClient(Java)