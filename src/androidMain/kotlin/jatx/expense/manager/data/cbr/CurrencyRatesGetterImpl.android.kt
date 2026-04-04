package jatx.expense.manager.data.cbr

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

actual fun httpClient() = HttpClient(Android)