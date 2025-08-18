package jatx.expense.manager.domain.usecase

import jatx.expense.manager.di.AppScope
import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.repository.PaymentRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SaveExpenseTableToDBUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend fun execute(expenseTable: ExpenseTable) {
        println("dropping table")
        paymentRepository.dropTableIfExists()
        println("creating table")
        paymentRepository.createTableIfNotExists()
        println("inserting data")
        paymentRepository.insertPayments(expenseTable.allPayments)
    }
}