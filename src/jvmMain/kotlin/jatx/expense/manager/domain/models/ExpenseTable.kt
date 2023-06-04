package jatx.expense.manager.domain.models

import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.domain.util.zeroDate
import jatx.expense.manager.res.totalCardName
import jatx.expense.manager.res.totalCategory
import java.util.*

data class CellKey(
    val cardName: String,
    val category: String,
    val monthKey: Int
)

data class ExpenseTable(
    private val allCells: Map<CellKey, ExpenseEntry>,
    private val _allDates: List<Date>,
    private val _allRowKeys: List<RowKey>
) {
    val allDates: List<Date> by lazy {
        val result = arrayListOf<Date>()
        result.add(zeroDate)
        result.addAll(_allDates)
        result.sortedBy { it.monthKey }
    }

    val allRowKeys: List<RowKey> by lazy {
            val result = arrayListOf<RowKey>()
            result.addAll(_allRowKeys)
            val totalKeys = _allRowKeys
                .distinctBy { it.rowKeyInt.cardNameKey }
                .map { RowKey(it.cardName, totalCategory, makeTotalRowKey(it.rowKeyInt.cardNameKey)) }
            result.addAll(totalKeys)
            result.add(RowKey(totalCardName, totalCategory, 0))
            result.sortedBy { it.rowKeyInt }
        }
    fun getCell(rowKey: RowKey, date: Date): ExpenseEntry {
        if (date.time == zeroDate.time) {
            val payments = _allDates
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

        if (rowKey.cardName == totalCardName && rowKey.category == totalCategory) {
            val payments = _allRowKeys
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
            val payments = _allRowKeys
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