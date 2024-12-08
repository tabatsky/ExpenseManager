package jatx.expense.manager.domain.usecase

import jatx.expense.manager.domain.cbr.CurrencyRatesGetter
import me.tatarka.inject.annotations.Inject

@Inject
class GetCurrencyRateUseCase(
    private val currencyRatesGetter: CurrencyRatesGetter
) {
    suspend fun execute() = currencyRatesGetter.getCurrencyRates()
}