package jatx.expense.manager.domain.usecase

import jatx.expense.manager.domain.cbr.CurrencyRatesGetter

class GetCurrencyRateUseCase(
    private val currencyRatesGetter: CurrencyRatesGetter
) {
    suspend fun execute() = currencyRatesGetter.getCurrencyRates()
}