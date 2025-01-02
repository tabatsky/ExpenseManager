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
import kotlin.math.roundToInt

data class CellKey(
    val cardName: String,
    val category: String,
    val monthKey: Int
)

data class ExpenseTable(
    private val allCells: Map<CellKey, ExpenseEntry>,
    val dates: List<Date>,
    val rowKeys: List<RowKey>,
    val currencyRates: Map<String, Float> = mapOf(),
    val version: Int = 0
) {
    val cellCount = allCells.size

    fun overallPieChartData(showSkipped: Boolean) = rowKeys
        .map { "${it.cardName} - ${it.category}" }
        .map { label ->
            val amount = dates
                .sumOf {
                    pieChartDataNotFiltered(it, null, showSkipped)
                        .find { it.first == label }
                        ?.second ?: 0
                }
            label to amount
        }
        .filter {
            it.second > 0
        }
        .sortedBy { -it.second }

    fun pieChartData(date: Date, date2: Date? = null, showSkipped: Boolean) =
        pieChartDataNotFiltered(date, date2, showSkipped)
            .filter {
                it.second > 0
            }

    private fun pieChartDataNotFiltered(date: Date, date2: Date? = null, showSkipped: Boolean) =
        if (showSkipped) {
            rowKeys
                .asSequence()
                .filter { it.category !in setOf(investCategory, usdCategory, cnyCategory) }
                .map { rowKey ->
                    val amount = if (date2 == null) {
                         getCell(rowKey, date)
                            .payments
                            .sumOf { it.rurAmount }
                    } else {
                        dates
                            .filter {
                                it.monthKey >= date.monthKey && it.monthKey <= date2.monthKey
                            }
                            .map { getCell(rowKey, it) }
                            .flatMap { it.payments }
                            .sumOf { it.rurAmount }
                    }
                    val label = "${rowKey.cardName} - ${rowKey.category}"
                    label to amount
                }
                .sortedBy { -it.second }
                .toList()
        } else {
            rowKeys
                .asSequence()
                .filter { it.category !in setOf(investCategory, usdCategory, cnyCategory) }
                .flatMap { rowKey ->
                    if (date2 == null) {
                        listOfNotNull(allCells[CellKey(rowKey.cardName, rowKey.category, date.monthKey)])
                    } else {
                        dates
                            .filter {
                                it.monthKey >= date.monthKey && it.monthKey <= date2.monthKey
                            }
                            .mapNotNull {
                                allCells[CellKey(rowKey.cardName, rowKey.category, it.monthKey)]
                            }
                    }
                }
                .map { expenseEntry ->
                    val amount = expenseEntry
                        .filterTotalPlus()
                        .filter { it.rurAmount > 0 }
                        .sumOf { it.rurAmount }
                    val label = "${expenseEntry.cardName} - ${expenseEntry.category}"
                    label to amount
                }
                .groupBy { it.first }
                .map { it.key to it.value.sumOf { it.second } }
                .sortedBy { -it.second }
                .toList()
        }

    fun overallPieChartDataByComment() = dates
        .flatMap {
            pieChartDataByCommentNotFiltered(it)
                .filter {
                    it.second > 0
                }
        }
        .groupBy {
            it.first
        }
        .map {
            it.key to it.value.sumOf { it.second }
        }
        .sortedBy { -it.second }

    fun pieChartDataByComment(date: Date) =
        pieChartDataByCommentNotFiltered(date)
            .filter {
                it.second > 0
            }

    private fun pieChartDataByCommentNotFiltered(date: Date) =
        rowKeys
            .asSequence()
            .filter { it.category !in setOf(investCategory, usdCategory, cnyCategory) }
            .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
            .flatMap { expenseEntry ->
                val amounts = expenseEntry
                    .filterTotalPlus()
                    .filter { it.rurAmount > 0 }
                    .groupBy {
                        it
                            .comment.split("-").first().trim()
                            .takeIf {
                                it != defaultCommentPositiveAmount &&
                                        it != defaultCommentNegativeAmount
                            } ?: it.category.utf8toCP1251()
                    }
                    .map {
                        it.key.cp1251toUTF8() to it.value.sumOf { it.rurAmount }
                    }
                amounts
            }
            .sortedBy { -it.second }
            .toList()

    fun overallPieChartDataByCommentMinus() = dates
        .flatMap {
            pieChartDataByCommentMinusNotFiltered(it)
                .filter {
                    it.second > 0
                }
        }
        .groupBy {
            it.first
        }
        .map {
            it.key to it.value.sumOf { it.second }
        }
        .sortedBy { -it.second }

    fun pieChartDataByCommentMinus(date: Date, date2: Date? = null) =
        pieChartDataByCommentMinusNotFiltered(date, date2)
            .filter {
                it.second > 0
            }

    private fun pieChartDataByCommentMinusNotFiltered(date: Date, date2: Date? = null) =
        rowKeys
            .asSequence()
            .flatMap { rowKey ->
                if (date2 == null) {
                    listOfNotNull(allCells[CellKey(rowKey.cardName, rowKey.category, date.monthKey)])
                } else {
                    dates
                        .filter {
                            it.monthKey >= date.monthKey && it.monthKey <= date2.monthKey
                        }
                        .mapNotNull {
                            allCells[CellKey(rowKey.cardName, rowKey.category, it.monthKey)]
                        }
                }
            }
            .flatMap { expenseEntry ->
                val amounts = expenseEntry
                    .filterTotalMinus()
                    .map { it.copy(currencyRate = currencyRates[it.currency] ?: 1f) }
                    .filter { it.rurAmount < 0 }
                    .groupBy {
                        it
                            .comment.split("-").first().trim()
                            .takeIf {
                                it != defaultCommentPositiveAmount &&
                                        it != defaultCommentNegativeAmount
                            } ?: it.category.utf8toCP1251()
                    }
                    .map {
                        it.key.cp1251toUTF8() to it.value.sumOf { it.rurAmount }
                    }
                amounts
            }
            .groupBy { it.first }
            .map { it.key to it.value.sumOf { -it.second } }
            .sortedBy { -it.second }
            .toList()

    fun byMonthData() = let { table ->
            table.dates.map { date ->
                val plusAmount = overallTotalPlusPayments(date)
                    .map { it.copy(currencyRate = currencyRates[it.currency] ?: 1f) }
                    .sumOf { it.rurAmount }
                val minusAmount = overallTotalMinusPayments(date)
                    .map { it.copy(currencyRate = currencyRates[it.currency] ?: 1f) }
                    .sumOf { it.rurAmount }
                val plus = plusAmount to "${date.formattedMonthAndYear}    $plusAmount"
                val minus = minusAmount to "${date.formattedMonthAndYear}    $minusAmount"
                plus to minus
            }.let { list ->
                val plusMean = list.map { it.first.first }.average().roundToInt()
                val minusMean = list.map { it.second.first }.average().roundToInt()
                val plus = plusMean to "среднее   $plusMean"
                val minus = minusMean to "среднее   $minusMean"
                list + (plus to minus)
            }.flatMap {
                val plus = it.first
                val minus = it.second
                val plusBar = BarChartData.Bar(
                    plus.first.toFloat(),
                    Color.Blue,
                    plus.second
                )
                val minusBar = BarChartData.Bar(
                    abs(minus.first.toFloat()),
                    Color.Red,
                    minus.second
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
        .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
        .flatMap { expenseEntry ->
            expenseEntry.filterTotalPlus()
        }
        .filter { it.amount > 0 }
        .sortedBy { it.date.time }

    private fun totalPlusPayments(date: Date, cardName: String) = rowKeys
        .filter { it.cardName == cardName }
        .filter { it.category !in setOf(investCategory, usdCategory, cnyCategory) }
        .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
        .flatMap { expenseEntry ->
            expenseEntry.filterTotalPlus()
        }
        .filter { it.amount > 0 }
        .sortedBy { it.date.time }

    private fun ExpenseEntry.filterTotalPlus() = let { expenseEntry ->
        expenseEntry
            .payments
            .filter {
                !SkipSet.containsLabel(expenseEntry.label)
                        && !ReduceSet.containsKey(expenseEntry.cardName)
                        || it.comment.cp1251toUTF8().startsWith(writeOffComment)
                        || it.comment.cp1251toUTF8().startsWith(salaryComment)
                        || it.comment.cp1251toUTF8().startsWith(giftComment)
                        || it.comment.cp1251toUTF8().startsWith(gift2Comment)
                        || it.comment.cp1251toUTF8().startsWith(payComment)
            }
    }

    private fun overallTotalMinusPayments(date: Date) = rowKeys
        .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
        .flatMap { expenseEntry ->
            expenseEntry.filterTotalMinus()
        }
        .filter { it.amount < 0 }
        .sortedBy { it.date.time }

    private fun totalMinusPayments(date: Date, cardName: String) = rowKeys
        .filter { it.cardName == cardName }
        .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
        .flatMap { expenseEntry ->
            expenseEntry.filterTotalMinus()
        }
        .filter { it.amount < 0 }
        .sortedBy { it.date.time }

    private fun ExpenseEntry.filterTotalMinus() = let { expenseEntry ->
        expenseEntry
            .payments
            .filter {
                expenseEntry.category != incomingCategory
                        && !SkipSet.containsLabel(expenseEntry.label)
                        && !ReduceSet.containsKey(expenseEntry.cardName)
                        && expenseEntry.category !in setOf(investCategory, usdCategory, cnyCategory)
                        || IncomingSet.containsLabel(it.comment.cp1251toUTF8())
                        || it.comment.cp1251toUTF8().startsWith(salaryComment)
                        || it.comment.cp1251toUTF8().startsWith(returnComment)
            }
    }

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
            val payments = totalPlusPayments(date, rowKey.cardName)
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
            val payments = totalMinusPayments(date, rowKey.cardName)
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