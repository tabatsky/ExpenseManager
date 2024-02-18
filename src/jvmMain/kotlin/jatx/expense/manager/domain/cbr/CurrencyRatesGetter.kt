package jatx.expense.manager.domain.cbr

interface CurrencyRatesGetter {
    suspend fun getCurrencyRates(): Map<String, Float>
}