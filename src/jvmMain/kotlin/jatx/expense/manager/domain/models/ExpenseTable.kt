package jatx.expense.manager.domain.models

import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.domain.util.zeroDate
import jatx.expense.manager.res.*
import java.util.*

data class CellKey(
    val cardName: String,
    val category: String,
    val monthKey: Int
)

data class ExpenseTable(
    val allCells: Map<CellKey, ExpenseEntry>,
    val dates: List<Date>,
    val rowKeys: List<RowKey>
) {
    val allDates: List<Date> by lazy {
        val result = arrayListOf<Date>()
        result.add(zeroDate)
        result.addAll(dates)
        result.sortedBy { it.monthKey }
    }

    val allRowKeys: List<RowKey> by lazy {
            val result = arrayListOf<RowKey>()
            result.addAll(rowKeys)
            val totalKeys = rowKeys
                .distinctBy { it.rowKeyInt.cardNameKey }
                .map { RowKey(it.cardName, totalCategory, makeTotalRowKey(it.rowKeyInt.cardNameKey)) }
            result.addAll(totalKeys)
            result.add(RowKey(totalCardName, totalWithCashCategory, 0))
            result.add(RowKey(totalCardName, totalCategory, 1))
            result.add(RowKey(totalCardName, totalLohCategory, makeRowKey(0, lohKey)))
            result.sortedBy { it.rowKeyInt }
        }
    fun getCell(rowKey: RowKey, date: Date): ExpenseEntry {
        if (date.time == zeroDate.time) {
            val payments = dates
                .map { getCell(rowKey, it) }
                .flatMap { it.payments }
                .sortedBy { it.id }
            return ExpenseEntry(
                rowKey.cardName,
                rowKey.category,
                rowKey.rowKeyInt,
                zeroDate,
                payments
            )
        }

        if (rowKey.cardName == totalCardName && rowKey.category == totalWithCashCategory) {
            val payments = rowKeys
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .flatMap { it.payments }
                .sortedBy { it.id }
            return ExpenseEntry(
                totalCardName,
                totalCategory,
                0,
                date,
                payments
            )
        }

        if (rowKey.cardName == totalCardName && rowKey.category == totalCategory) {
            val payments = rowKeys
                .filter { it.cardName != cashCardName }
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .flatMap { it.payments }
                .sortedBy { it.id }
            return ExpenseEntry(
                totalCardName,
                totalCategory,
                0,
                date,
                payments
            )
        }

        if (rowKey.cardName == totalCardName && rowKey.category == totalLohCategory) {
            val payments = rowKeys
                .filter { it.category == lohCategory }
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .flatMap { it.payments }
                .sortedBy { it.id }
            return ExpenseEntry(
                totalCardName,
                totalCategory,
                0,
                date,
                payments
            )
        }

        if (rowKey.category == totalCategory) {
            val payments = rowKeys
                .filter { it.cardName == rowKey.cardName }
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .flatMap { it.payments }
                .sortedBy { it.id }
            return ExpenseEntry(
                rowKey.cardName,
                totalCategory,
                makeTotalRowKey(rowKey.rowKeyInt.cardNameKey),
                date,
                payments
            )
        }

        val result = allCells[CellKey(
            rowKey.cardName,
            rowKey.category,
            date.monthKey
        )]

        if (result != null) return result
        return ExpenseEntry.makeFromDouble(
            rowKey,
            date,
            0.0
        )
    }

    val allPayments: List<PaymentEntry>
        get() = allCells
            .entries
            .flatMap { it.value.payments }
}