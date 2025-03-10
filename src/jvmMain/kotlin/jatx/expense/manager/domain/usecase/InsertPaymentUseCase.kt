package jatx.expense.manager.domain.usecase

import jatx.expense.manager.di.AppScope
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.models.makeDefaultComment
import jatx.expense.manager.domain.repository.PaymentRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class InsertPaymentUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend fun execute(paymentEntry: PaymentEntry) =
        paymentRepository.insertPayment(
            paymentEntry.let {
                if (it.comment.trim().isEmpty())
                    it.copy(comment = makeDefaultComment(it.amount))
                else
                    it
            }
        )
}