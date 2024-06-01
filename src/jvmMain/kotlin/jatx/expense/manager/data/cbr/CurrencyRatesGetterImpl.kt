package jatx.expense.manager.data.cbr

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import jatx.expense.manager.domain.cbr.CurrencyRatesGetter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class CurrencyRatesGetterImpl: CurrencyRatesGetter {
    override suspend fun getCurrencyRates(): Map<String, Float> {
        return try {
            val httpClient = HttpClient(Java)
            val response = httpClient.get<String> {
                url("https://www.cbr-xml-daily.ru/latest.js")
            }

            val jsonObj = Json.decodeFromString<JsonObject>(response)
            val rates = jsonObj["rates"] as JsonObject
            val usdRate = 1f / rates["USD"].toString().toFloat()
            val cnyRate = 1f / rates["CNY"].toString().toFloat()

            mapOf(
                "RUR" to 1f,
                "USD" to usdRate,
                "CNY" to cnyRate
            )
        } catch (e: Exception) {
            e.printStackTrace()
            mapOf(
                "RUR" to 1f,
                "USD" to 1f,
                "CNY" to 1f
            )
        }
    }
}