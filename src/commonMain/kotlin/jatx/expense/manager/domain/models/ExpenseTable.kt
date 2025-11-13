package jatx.expense.manager.domain.models

import jatx.expense.manager.data.skipset.*
import jatx.expense.manager.domain.util.*
import jatx.expense.manager.res.*
import java.util.*

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

    fun overallPieChartData(showSkipped: Boolean, joinByCards: Boolean) = rowKeys
        .map {
            if (joinByCards) {
                it.category
            } else {
                "${it.cardName} - ${it.category}"
            }
        }
        .distinct()
        .map { label ->
            val amount = dates
                .sumOf {
                    pieChartDataNotFiltered(it, null, showSkipped, joinByCards)
                        .find { it.first == label }
                        ?.second ?: 0
                }
            label to amount
        }
        .filter {
            it.second > 0
        }
        .sortedBy { -it.second }

    fun pieChartData(date: Date, date2: Date? = null, showSkipped: Boolean, joinByCards: Boolean) =
        pieChartDataNotFiltered(date, date2, showSkipped, joinByCards)
            .filter {
                it.second > 0
            }

    private fun pieChartDataNotFiltered(date: Date, date2: Date? = null, showSkipped: Boolean, joinByCards: Boolean) =
        if (showSkipped) {
            rowKeys
                .asSequence()
                .filter { it.category !in setOf(investCategory, invest2Category, invest3Category, usdCategory, cnyCategory, uBTCCategory) }
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
                    val label = if (joinByCards) {
                        rowKey.category
                    } else {
                        "${rowKey.cardName} - ${rowKey.category}"
                    }
                    label to amount
                }
                .sortedBy { -it.second }
                .toList()
        } else {
            rowKeys
                .asSequence()
                .filter { it.category !in setOf(investCategory, invest2Category, invest3Category, usdCategory, cnyCategory, uBTCCategory) }
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
                    val label = if (joinByCards) {
                        expenseEntry.category
                    } else {
                        "${expenseEntry.cardName} - ${expenseEntry.category}"
                    }
                    label to amount
                }
                .groupBy { it.first }
                .map { it.key to it.value.sumOf { it.second } }
                .sortedBy { -it.second }
                .toList()
        }
            .groupBy { it.first }
            .toList()
            .map { it.first to it.second.sumOf { it.second } }

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

    fun pieChartDataByComment(date: Date, date2: Date? = null) =
        pieChartDataByCommentNotFiltered(date, date2)
            .filter {
                it.second > 0
            }

    private fun pieChartDataByCommentNotFiltered(date: Date, date2: Date? = null) =
        rowKeys
            .asSequence()
            .filter { it.category !in setOf(investCategory, invest2Category, invest3Category, usdCategory, cnyCategory, uBTCCategory) }
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
                    .filterTotalPlus()
                    .filter { it.rurAmount > 0 }
                    .groupBy {
                        it
                            .comment.let {
                                if (ExpenseCommentSet.labelMatching(it.cp1251toUTF8())) {
                                    it.split("-").last().trim()
                                } else {
                                    it.split("-").first().trim()
                                }
                            }
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
            .map { it.key to it.value.sumOf { it.second } }
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



    fun overallTotalPlusPayments(date: Date) = rowKeys
        .filter { it.category !in setOf(investCategory, invest2Category, invest3Category, usdCategory, cnyCategory, uBTCCategory) }
        .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
        .flatMap { expenseEntry ->
            expenseEntry.filterTotalPlus()
        }
        .filter { it.amount > 0 }
        .sortedBy { it.date.time }

    private fun totalPlusPayments(date: Date, cardName: String) = rowKeys
        .filter { it.cardName == cardName }
        .filter { it.category !in setOf(investCategory, invest2Category, invest3Category, usdCategory, cnyCategory, uBTCCategory) }
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
                        || ExpenseCommentSet.labelMatching(it.comment.cp1251toUTF8())
            }
    }

    fun overallTotalMinusPayments(date: Date) = rowKeys
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
                        && expenseEntry.category !in setOf(investCategory, invest2Category, invest3Category, usdCategory, cnyCategory, uBTCCategory)
                        || IncomingSet.containsLabel(it.comment.cp1251toUTF8())
                        || IncomingCommentSet.labelMatching(it.comment.cp1251toUTF8())
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
        if (lohCategoryEnabled) {
            result.add(RowKey(overallCardName, totalLohCategory, makeRowKey(0, lohKey)))
        }
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
        if (lohCategoryEnabled) {
            result.add(RowKey(overallCardName, totalLohCategory, makeRowKey(0, lohKey)))
        }
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
                .filter { it.category !in setOf(investCategory, invest2Category, invest3Category, usdCategory, cnyCategory, uBTCCategory) }
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
                .filter { it.category !in setOf(investCategory, invest2Category, invest3Category, usdCategory, cnyCategory, uBTCCategory) }
                .mapNotNull { allCells[CellKey(it.cardName, it.category, date.monthKey)] }
                .flatMap { it.payments }
                .filter { it.amount < 0 }
                .sortedBy { it.date.time }
            return ExpenseEntry(
                rowKey.cardName,
                totalMinus2Category,
                makeTotalMinus2RowKey(rowKey.rowKeyInt.cardNameKey),
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