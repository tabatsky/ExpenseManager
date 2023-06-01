package jatx.expense.manager.domain.models

import jatx.expense.manager.domain.util.monthKey
import java.util.*

typealias CellKey = Triple<String, String, Int>
typealias RowKey = Triple<String, String, Int>

data class ExpenseTable(
    private val allCells: Map<CellKey, ExpenseEntry>,
    val allDates: List<Date>,
    val allRowKeys: List<RowKey>
) {
    fun getCell(rowKey: RowKey, date: Date) =
        allCells[Triple(
            rowKey.first,
            rowKey.second,
            date.monthKey
        )]
            ?: ExpenseEntry.makeFromDouble(
                rowKey.first,
                rowKey.second,
                rowKey.third,
                date,
                0.0
            )

    val allPayments: List<PaymentEntry>
        get() = allCells
            .entries
            .flatMap { it.value.payments }
}