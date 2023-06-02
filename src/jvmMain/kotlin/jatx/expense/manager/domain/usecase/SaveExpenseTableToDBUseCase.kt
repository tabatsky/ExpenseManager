package jatx.expense.manager.domain.usecase

import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.repository.PaymentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SaveExpenseTableToDBUseCase(
    private val paymentRepository: PaymentRepository,
    private val coroutineScope: CoroutineScope
) {
    fun execute(expenseTable: ExpenseTable) = coroutineScope.launch {
        paymentRepository.dropTableIfExists()
        paymentRepository.createTableIfNotExists()
        paymentRepository.insertPayments(expenseTable.allPayments)
    }
}