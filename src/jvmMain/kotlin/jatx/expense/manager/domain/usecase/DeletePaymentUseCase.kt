package jatx.expense.manager.domain.usecase

import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.repository.PaymentRepository

class DeletePaymentUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend fun execute(paymentEntry: PaymentEntry) =
        paymentRepository.deletePayment(paymentEntry)
}