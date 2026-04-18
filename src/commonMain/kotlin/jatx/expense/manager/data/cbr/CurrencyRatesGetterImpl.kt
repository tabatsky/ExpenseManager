package jatx.expense.manager.data.cbr

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import jatx.expense.manager.di.AppScope
import jatx.expense.manager.domain.cbr.CurrencyRatesGetter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class CurrencyRatesGetterImpl: CurrencyRatesGetter {
    override suspend fun getCurrencyRates(): Map<String, Float> = withContext(Dispatchers.IO)  {
        val result = hashMapOf(
            "RUR" to 1f,
            "USD" to 1f,
            "CNY" to 1f,
            "uBTC" to 1f
        )

        try {
            val httpClient = httpClient()
            val response = httpClient.get {
                url("https://www.cbr-xml-daily.ru/latest.js")
            }

            val jsonObj = Json.decodeFromString<JsonObject>(response.bodyAsText())
            val rates = jsonObj["rates"] as JsonObject
            val usdRate = 1f / rates["USD"].toString().toFloat()
            val cnyRate = 1f / rates["CNY"].toString().toFloat()

            result["USD"] = usdRate
            result["CNY"] = cnyRate

            val responseBTC = httpClient.get {
                url("https://blockchain.info/ticker")
            }
            val jsonObjBTC = Json.decodeFromString<JsonObject>(responseBTC.bodyAsText())
            val rub = jsonObjBTC["RUB"] as JsonObject
            val btcRate = rub["last"].toString().toFloat()
            val uBTCRate = btcRate / 1000000f

            result["uBTC"] = uBTCRate
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        result
    }
}

expect fun httpClient(): HttpClient