package jatx.expense.manager.domain.usecase

import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.repository.PaymentRepository
import me.tatarka.inject.annotations.Inject

@Inject
class UpdatePaymentUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend fun execute(paymentEntry: PaymentEntry) =
        paymentRepository.updatePayment(paymentEntry)
}