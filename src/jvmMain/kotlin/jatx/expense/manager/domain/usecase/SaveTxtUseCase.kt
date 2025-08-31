package jatx.expense.manager.domain.usecase

import TxtSaver
import jatx.expense.manager.di.AppScope
import jatx.expense.manager.domain.models.PaymentEntry
import me.tatarka.inject.annotations.Inject
import java.util.Date

@AppScope
@Inject
class SaveTxtUseCase(
    private val txtSaver: TxtSaver
) {
    fun execute(payments: List<PaymentEntry>, cardName: String, category: String, date: Date) =
        txtSaver.savePayments(payments, cardName, category, date)
}