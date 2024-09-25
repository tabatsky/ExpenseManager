package jatx.expense.manager.domain.models

import androidx.compose.ui.graphics.Color
import com.github.tehras.charts.bar.BarChartData
import jatx.expense.manager.data.skipset.IncomingSet
import jatx.expense.manager.data.skipset.ReduceSet
import jatx.expense.manager.data.skipset.SkipSet
import jatx.expense.manager.domain.util.*
import jatx.expense.manager.res.*
import java.util.*
import kotlin.math.abs

data class CellKey(
    val cardName: String,
    val category: String,
    val monthKey: Int
)

data class ExpenseTable(
    private val allCells: Map<CellKey, ExpenseEntry>,
    val dates: List<Date>,
    val rowKeys: List<RowKey>,
    val currencyRates: Map<String, Float> = mapOf()
) {
    val cellCount = allCells.size

    fun overallPieChartData(showSkipped: Boolean) = rowKeys
        .map { "${it.cardName} - ${it.category}" }
        .map { label ->
            val amount = dates
                .sumOf {
                    pieChartDataNotFiltered(it, showSkipped)
                        .find { it.first == label }
                        ?.second ?: 0
                }
            label to amount
        }
        .filter {
            it.second > 0
        }
        .sortedBy { -it.second }

    fun pieChartData(date: Date, showSkipped: Boolean) =
        pieChartDataNotFiltered(date, showSkipped)
            .filter {
                it.second > 0
            }

    private fun pieChartDataNotFiltered(date: Date, showSkipped: Boolean) =
        if (showSkipped) {
            rowKeys
                .asSequence()
                .filter { it.category !in setOf(investCategory, usdCategory, cnyCategory) }
                .map {
                    val amount = getCell(it, date)
                        .payments
                        .sumOf { it.rurAmount }
                    val label = "${it.cardName} - ${it.category}"
                    label to amount
                }
                .sortedBy { -it.second }
                .toList()
        } else {
            rowKeys
                .filter { it.category !in setOf(investCategory, usdCategory, cnyCategory) }
                .filter { !SkipSet.containsLabel(it.label) }
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .map { expenseEntry ->
                    val amount = expenseEntry
                        .payments
                        .filter {
                            !ReduceSet.containsKey(expenseEntry.cardName)
                                    || it.comment.cp1251toUTF8().startsWith(writeOffComment)
                        }
                        .filter { it.rurAmount > 0 }
                        .sumOf { it.rurAmount }
                    val label = "${expenseEntry.cardName} - ${expenseEntry.category}"
                    label to amount
                }
                .sortedBy { -it.second }
        }

    fun byMonthData() = let { table ->
            table.dates.drop(1).flatMap { date ->
                val plusAmount = overallTotalPlusPayments(date)
                    .sumOf { it.rurAmount }
                val minusAmount = overallTotalMinusPayments(date)
                    .sumOf { it.rurAmount }
                val plusBar = BarChartData.Bar(
                    plusAmount.toFloat(),
                    Color.Blue,
                    "${date.formattedMonthAndYear}    $plusAmount"
                )
                val minusBar = BarChartData.Bar(
                    abs(minusAmount.toFloat()),
                    Color.Red,
                    "${date.formattedMonthAndYear}    $minusAmount"
                )
                val emptyBar = BarChartData.Bar(
                    0f,
                    Color.White,
                    ""
                )
                listOf(plusBar, minusBar, emptyBar)
            }
        }

    private fun overallTotalPlusPayments(date: Date) = rowKeys
        .filter { it.category !in setOf(investCategory, usdCategory, cnyCategory) }
        .filter { !SkipSet.containsLabel(it.label) }
        .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
        .flatMap { expenseEntry ->
            expenseEntry
                .payments
                .filter {
                    !ReduceSet.containsKey(expenseEntry.cardName)
                            || it.comment.cp1251toUTF8().startsWith(writeOffComment)
                }
        }
        .filter { it.amount > 0 }
        .sortedBy { it.date.time }

    private fun overallTotalMinusPayments(date: Date) = rowKeys
        .filter { !SkipSet.containsLabel(it.label) }
        .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
        .flatMap { expenseEntry ->
            expenseEntry.payments
                .filter {
                    expenseEntry.category != incomingCategory
                            && !ReduceSet.containsKey(expenseEntry.cardName)
                            && expenseEntry.category !in setOf(investCategory, usdCategory, cnyCategory)
                            || IncomingSet.containsLabel(it.comment.cp1251toUTF8())
                            || it.comment.cp1251toUTF8().startsWith(salaryComment)
                }
        }
        .filter { it.amount < 0 }
        .sortedBy { it.date.time }

    val datesWithZeroDate: List<Date> by lazy {
        val result = arrayListOf<Date>()
        result.add(zeroDate)
        result.addAll(dates)
        result.sortedBy { it.monthKey }
    }

    val rowKeysWithTotals: List<RowKey> by lazy {
        val result = arrayListOf<RowKey>()
        result.addAll(rowKeys)
        val totalKeys = rowKeys
            .distinctBy { it.rowKeyInt.cardNameKey }
            .map { RowKey(it.cardName, totalCategory, makeTotalRowKey(it.rowKeyInt.cardNameKey)) }
        result.addAll(totalKeys)
        val totalPlusKeys = rowKeys
            .filter { !ReduceSet.containsKey(it.cardName) }
            .distinctBy { it.rowKeyInt.cardNameKey }
            .map { RowKey(it.cardName, totalPlusCategory, makeTotalPlusRowKey(it.rowKeyInt.cardNameKey)) }
        result.addAll(totalPlusKeys)
        val totalPlus2Keys = rowKeys
            .filter { !ReduceSet.containsKey(it.cardName) }
            .distinctBy { it.rowKeyInt.cardNameKey }
            .map { RowKey(it.cardName, totalPlus2Category, makeTotalPlus2RowKey(it.rowKeyInt.cardNameKey)) }
        result.addAll(totalPlus2Keys)
        val totalMinusKeys = rowKeys
            .filter { !ReduceSet.containsKey(it.cardName) }
            .distinctBy { it.rowKeyInt.cardNameKey }
            .map { RowKey(it.cardName, totalMinusCategory, makeTotalMinusRowKey(it.rowKeyInt.cardNameKey)) }
        result.addAll(totalMinusKeys)
        val totalMinus2Keys = rowKeys
            .filter { !ReduceSet.containsKey(it.cardName) }
            .distinctBy { it.rowKeyInt.cardNameKey }
            .map { RowKey(it.cardName, totalMinus2Category, makeTotalMinus2RowKey(it.rowKeyInt.cardNameKey)) }
        result.addAll(totalMinus2Keys)
        result.add(RowKey(overallCardName, totalCategory, 0))
        result.add(RowKey(overallCardName, totalPlusCategory, makeTotalPlusRowKey(0)))
        result.add(RowKey(overallCardName, totalMinusCategory, makeTotalPlusRowKey(0)))
        result.add(RowKey(overallCardName, totalLohCategory, makeRowKey(0, lohKey)))
        result.sortedBy {
            val key = it.rowKeyInt
            val sortKey = if (key % 1000 <= 600) {
                (key / 1000) * 1000 + ((key % 1000) % 100) + ((key % 1000) / 100) * 0.1
            } else {
                key.toDouble()
            }
            sortKey
        }
    }

    val rowKeysWithTotalsNoPlusMinus: List<RowKey> by lazy {
        val result = arrayListOf<RowKey>()
        result.addAll(rowKeys)
        val totalKeys = rowKeys
            .distinctBy { it.rowKeyInt.cardNameKey }
            .map { RowKey(it.cardName, totalCategory, makeTotalRowKey(it.rowKeyInt.cardNameKey)) }
        result.addAll(totalKeys)
        result.add(RowKey(overallCardName, totalCategory, 0))
        result.add(RowKey(overallCardName, totalLohCategory, makeRowKey(0, lohKey)))
        result.sortedBy {
            val key = it.rowKeyInt
            val sortKey = if (key % 1000 < 800) {
                (key / 1000) * 1000 + ((key % 1000) % 100) + ((key % 1000) / 100) * 0.1
            } else {
                key.toDouble()
            }
            sortKey
        }
    }
    val allPayments: List<PaymentEntry>
        get() = allCells
            .entries
            .flatMap { it.value.payments }

    val statisticsEntryByComment: ExpenseEntry
        get() {
            val allPaymentsWithComments = allPayments
                .map {
                    val comment = it
                    .comment.split("-").first().trim()
                        .takeIf {
                            it != defaultCommentPositiveAmount &&
                                    it != defaultCommentNegativeAmount
                        } ?: it.category.utf8toCP1251()
                    it.copy(comment = comment)
                }
            val uniqueComments = allPaymentsWithComments
                .map { it.comment }
                .distinct()
            val paymentsForStatistics = uniqueComments.map { _comment ->
                val amount = allPaymentsWithComments
                    .filter { it.comment == _comment }
                    .sumOf { it.amount }
                PaymentEntry(
                    id = -1L,
                    cardName = "",
                    category = _comment.cp1251toUTF8(),
                    rowKeyInt = 0,
                    date = Date(),
                    amount = amount,
                    comment = _comment
                ).let {
                    it.copy(currencyRate = currencyRates[it.currency] ?: 1f)
                }
            }
            return ExpenseEntry(
                cardName = "",
                category = "",
                rowKeyInt = 0,
                date = Date(),
                _payments = paymentsForStatistics.sortedBy { it.rurAmount },
                currencyRates = currencyRates,
                needSortByDate = false
            )
        }

    val statisticsEntryByCategory: ExpenseEntry
        get() {
            val allPaymentsWithComments = allPayments
                .map {
                    val comment =  it.category.utf8toCP1251()
                    it.copy(comment = comment)
                }
            val uniqueComments = allPaymentsWithComments
                .map { it.comment }
                .distinct()
            val paymentsForStatistics = uniqueComments.map { _comment ->
                val amount = allPaymentsWithComments
                    .filter { it.comment == _comment }
                    .sumOf { it.amount }
                PaymentEntry(
                    id = -1L,
                    cardName = "",
                    category = _comment.cp1251toUTF8(),
                    rowKeyInt = 0,
                    date = Date(),
                    amount = amount,
                    comment = _comment
                ).let {
                    it.copy(currencyRate = currencyRates[it.currency] ?: 1f)
                }
            }
            return ExpenseEntry(
                cardName = "",
                category = "",
                rowKeyInt = 0,
                date = Date(),
                _payments = paymentsForStatistics.sortedBy { it.rurAmount },
                currencyRates = currencyRates,
                needSortByDate = false
            )
        }

    fun getCell(rowKey: RowKey, date: Date): ExpenseEntry {
        if (date.time == zeroDate.time) {
            val payments = dates
                .map { getCell(rowKey, it) }
                .flatMap { it.payments }
                .sortedBy { it.date.time }
            return ExpenseEntry(
                rowKey.cardName,
                rowKey.category,
                rowKey.rowKeyInt,
                zeroDate,
                payments,
                currencyRates
            )
        }

        if (rowKey.cardName == overallCardName && rowKey.category == totalCategory) {
            val payments = rowKeys
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .flatMap { it.payments }
                .sortedBy { it.date.time }
            return ExpenseEntry(
                overallCardName,
                totalCategory,
                0,
                date,
                payments,
                currencyRates
            )
        }

        if (rowKey.cardName == overallCardName && rowKey.category == totalPlusCategory) {
            val payments = overallTotalPlusPayments(date)
            return ExpenseEntry(
                rowKey.cardName,
                totalPlusCategory,
                makeTotalPlusRowKey(rowKey.rowKeyInt.cardNameKey),
                date,
                payments,
                currencyRates
            )
        }

        if (rowKey.cardName == overallCardName && rowKey.category == totalMinusCategory) {
            val payments = overallTotalMinusPayments(date)
            return ExpenseEntry(
                rowKey.cardName,
                totalMinusCategory,
                makeTotalMinusRowKey(rowKey.rowKeyInt.cardNameKey),
                date,
                payments,
                currencyRates
            )
        }

        if (rowKey.cardName == overallCardName && rowKey.category == totalLohCategory) {
            val payments = rowKeys
                .filter { it.category == lohCategory }
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .flatMap { it.payments }
                .sortedBy { it.date.time }
            return ExpenseEntry(
                overallCardName,
                totalCategory,
                0,
                date,
                payments,
                currencyRates
            )
        }

        if (rowKey.category == totalCategory) {
            val payments = rowKeys
                .filter { it.cardName == rowKey.cardName }
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .flatMap { it.payments }
                .sortedBy { it.date.time }
            return ExpenseEntry(
                rowKey.cardName,
                totalCategory,
                makeTotalRowKey(rowKey.rowKeyInt.cardNameKey),
                date,
                payments,
                currencyRates
            )
        }

        if (rowKey.category == totalPlusCategory) {
            val payments = rowKeys
                .filter { it.cardName == rowKey.cardName }
                .filter { it.category !in setOf(investCategory, usdCategory, cnyCategory) }
                .filter { !SkipSet.containsLabel(it.label) }
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .flatMap { it.payments }
                .filter { it.amount > 0 }
                .sortedBy { it.date.time }
            return ExpenseEntry(
                rowKey.cardName,
                totalPlusCategory,
                makeTotalPlusRowKey(rowKey.rowKeyInt.cardNameKey),
                date,
                payments,
                currencyRates
            )
        }

        if (rowKey.category == totalPlus2Category) {
            val payments = rowKeys
                .filter { it.cardName == rowKey.cardName }
                .filter { it.category !in setOf(investCategory, usdCategory, cnyCategory) }
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .flatMap { it.payments }
                .filter { it.amount > 0 }
                .sortedBy { it.date.time }
            return ExpenseEntry(
                rowKey.cardName,
                totalPlus2Category,
                makeTotalPlus2RowKey(rowKey.rowKeyInt.cardNameKey),
                date,
                payments,
                currencyRates
            )
        }

        if (rowKey.category == totalMinusCategory) {
            val payments = rowKeys
                .filter { it.cardName == rowKey.cardName }
                .filter { !ReduceSet.containsKey(it.cardName) }
                .filter { it.category !in setOf(investCategory, usdCategory, cnyCategory) }
                .filter { !SkipSet.containsLabel(it.label) }
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .flatMap { expenseEntry ->
                        expenseEntry.payments
                        .filter {
                            expenseEntry.category != incomingCategory
                                    || IncomingSet.containsLabel(it.comment.cp1251toUTF8())
                                    || it.comment.cp1251toUTF8().startsWith(salaryComment)
                        }
                }
                .filter { it.amount < 0 }
                .sortedBy { it.date.time }
            return ExpenseEntry(
                rowKey.cardName,
                totalMinusCategory,
                makeTotalMinusRowKey(rowKey.rowKeyInt.cardNameKey),
                date,
                payments,
                currencyRates
            )
        }

        if (rowKey.category == totalMinus2Category) {
            val payments = rowKeys
                .filter { it.cardName == rowKey.cardName }
                .filter { it.category !in setOf(investCategory, usdCategory, cnyCategory) }
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .flatMap { it.payments }
                .filter { it.amount < 0 }
                .sortedBy { it.date.time }
            return ExpenseEntry(
                rowKey.cardName,
                totalMinusCategory,
                makeTotalMinusRowKey(rowKey.rowKeyInt.cardNameKey),
                date,
                payments,
                currencyRates
            )
        }

        val result = allCells[CellKey(
            rowKey.cardName,
            rowKey.category,
            date.monthKey
        )]?.copy(currencyRates = currencyRates)

        if (result != null) return result
        return ExpenseEntry.makeFromDouble(
            rowKey,
            date,
            0.0
        )
    }
}