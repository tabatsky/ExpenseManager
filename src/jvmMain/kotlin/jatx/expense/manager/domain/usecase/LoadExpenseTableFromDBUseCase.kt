package jatx.expense.manager.domain.usecase

import jatx.expense.manager.domain.models.CellKey
import jatx.expense.manager.domain.models.ExpenseEntry
import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.models.RowKey
import jatx.expense.manager.domain.repository.PaymentRepository
import jatx.expense.manager.domain.util.dateFromMonthKey
import jatx.expense.manager.domain.util.monthKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*

class LoadExpenseTableFromDBUseCase(
    private val paymentRepository: PaymentRepository
) {
    fun execute(): Flow<ExpenseTable> = flow {
        val allPayments = paymentRepository.selectAll()
        val allRowKeys = allPayments
            .map { RowKey(it.cardName, it.category, it.rowKeyInt) }
            .distinct()
            .sortedBy { it.rowKeyInt }
        val minMonthKey = allPayments
            .map { it.date.monthKey }
            .minOrNull() ?: Date().monthKey
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
                    payments = payments
                )
                allCells[CellKey(expenseEntry.cardName, expenseEntry.category, monthKey)] = expenseEntry
            }
        }
        val expenseTable = ExpenseTable(
            allCells,
            allMonthKeys.map { it.dateFromMonthKey },
            allRowKeys
        )
        emit(expenseTable)
    }.flowOn(Dispatchers.IO)
}