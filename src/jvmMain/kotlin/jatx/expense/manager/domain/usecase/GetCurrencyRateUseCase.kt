package jatx.expense.manager.domain.usecase

import jatx.expense.manager.di.AppScope
import jatx.expense.manager.domain.cbr.CurrencyRatesGetter
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class GetCurrencyRateUseCase(
    private val currencyRatesGetter: CurrencyRatesGetter
) {
    suspend fun execute() = currencyRatesGetter.getCurrencyRates()
}