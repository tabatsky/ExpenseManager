package jatx.expense.manager.domain.models

import androidx.compose.ui.graphics.Color
import com.github.tehras.charts.bar.BarChartData
import jatx.expense.manager.domain.util.formattedMonthAndYear
import kotlin.math.abs
import kotlin.math.roundToInt

fun ExpenseTable.byMonthData() = let { table ->
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