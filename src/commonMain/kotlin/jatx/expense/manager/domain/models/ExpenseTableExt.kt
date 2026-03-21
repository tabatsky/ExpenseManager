package jatx.expense.manager.domain.models

import androidx.compose.ui.graphics.Color
import com.github.tehras.charts.bar.BarChartData
import jatx.expense.manager.domain.util.cp1251toUTF8
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.res.rusLowercase
import kotlin.math.abs
import kotlin.math.roundToInt

fun ExpenseTable.byMonthData(filter: String = "") = let { table ->
    println(filter.rusLowercase())
    table.dates.map { date ->
        val plusAmount = overallTotalPlusPayments(date)
            .filter { paymentEntry ->
                val comment = "${paymentEntry.category} - ${paymentEntry.comment.cp1251toUTF8()}"
                var match = false
                filter.split("+").forEach {
                    match = match || comment.rusLowercase().contains(it.rusLowercase())
                }
                match
            }
            .map { it.copy(currencyRate = currencyRates[it.currency] ?: 1f) }
            .sumOf { it.rurAmount }
        val minusAmount = overallTotalMinusPayments(date)
            .filter { paymentEntry ->
                val comment = "${paymentEntry.category} - ${paymentEntry.comment.cp1251toUTF8()}"
                var match = false
                filter.split("+").forEach {
                    match = match || comment.rusLowercase().contains(it.rusLowercase())
                }
                match
            }
            .map { it.copy(currencyRate = currencyRates[it.currency] ?: 1f) }
            .sumOf { it.rurAmount }
        val plus = plusAmount to "${date.formattedMonthAndYear}    $plusAmount"
        val minus = minusAmount to "${date.formattedMonthAndYear}    $minusAmount"
        plus to minus
    }.let { list ->
        val plusMean = list
            .map { it.first.first }
            .filter { it > 0 }
            .takeIf { it.isNotEmpty() }
            ?.average()
            ?.roundToInt() ?: 0
        val minusMean = list
            .map { it.second.first }
            .filter { it < 0 }
            .takeIf { it.isNotEmpty() }
            ?.average()
            ?.roundToInt() ?: 0
        val plus = plusMean to "среднее   $plusMean"
        val minus = minusMean to "среднее   $minusMean"
        list + (plus to minus)
    }.flatMap {
        val plus = it.first
        val minus = it.second
//        println("$plus $minus")
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