package jatx.expense.manager.domain.usecase

import jatx.expense.manager.di.AppScope
import jatx.expense.manager.domain.models.CellKey
import jatx.expense.manager.domain.models.ExpenseEntry
import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.models.RowKey
import jatx.expense.manager.domain.repository.PaymentRepository
import jatx.expense.manager.domain.util.dateFromMonthKey
import jatx.expense.manager.domain.util.monthKey
import me.tatarka.inject.annotations.Inject
import java.util.*

@AppScope
@Inject
class LoadExpenseTableFromDBUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend fun execute(): ExpenseTable = run {
        val allPayments = paymentRepository.selectAll()
        val allRowKeys = allPayments
            .map { RowKey(it.cardName, it.category, it.rowKeyInt) }
            .distinct()
            .sortedBy { it.rowKeyInt }
        val minMonthKey = allPayments.minOfOrNull { it.date.monthKey } ?: Date().monthKey
        val maxMonthKey = Date().monthKey
        val allMonthKeys = (minMonthKey .. maxMonthKey).toList()

        val allCells = hashMapOf<CellKey, ExpenseEntry>()

        allRowKeys.forEach { rowKey ->
            allMonthKeys.forEach { monthKey ->
                val payments = allPayments
                    .filter { it.rowKeyInt == rowKey.rowKeyInt && it.date.monthKey == monthKey }
                    .sortedBy { it.id }
                val expenseEntry = ExpenseEntry(
                    cardName = rowKey.cardName,
                    category = rowKey.category,
                    rowKeyInt = rowKey.rowKeyInt,
                    date = monthKey.dateFromMonthKey,
                    _payments = payments
                )
                allCells[CellKey(expenseEntry.cardName, expenseEntry.category, monthKey)] = expenseEntry
            }
        }
        val expenseTable = ExpenseTable(
            allCells,
            allMonthKeys.map { it.dateFromMonthKey },
            allRowKeys
        )
        expenseTable
    }
}