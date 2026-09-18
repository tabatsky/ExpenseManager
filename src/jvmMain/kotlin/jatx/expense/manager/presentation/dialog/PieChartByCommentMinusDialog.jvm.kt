package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.domain.util.dateFromMonthKey
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.res.*
import java.util.*

@Composable
actual fun PieChartByCommentMinusDialogWrapper() {
    val expenseViewModel = appComponent.expenseViewModel

    val show by expenseViewModel.needShowPieChartByCommentMinusDialog.collectAsState()

    fun close() {
        expenseViewModel.showPieChartByCommentMinusDialog(false)
    }

    if (show) {
        val dialogState = rememberDialogState()
        dialogState.size = DpSize(pieChartDialogWidth, pieChartDialogHeight)

        val monthKey by expenseViewModel.pieChartMonthKey.collectAsState()
        val monthKey2 by expenseViewModel.pieChartMonthKey2.collectAsState()

        val labelMonthKey = if (monthKey <= Date().monthKey)
            monthKey.dateFromMonthKey.formattedMonthAndYear
        else
            labelOverallTime

        val labelMonthKey2 = monthKey2?.dateFromMonthKey?.formattedMonthAndYear ?: labelNotSet

        val monthCount = ((monthKey2 ?: monthKey) - monthKey + 1).takeIf { it > 1 } ?: 1

        val pieChartData = if (monthKey <= Date().monthKey)
            expenseViewModel.pieChartDataByCommentMinus(monthKey.dateFromMonthKey, monthKey2?.dateFromMonthKey)
        else
            expenseViewModel.overallPieChartDataByCommentMinus()

        val checkedMap = remember { mutableStateMapOf<Int, Boolean>() }

        val count = pieChartData.size
        val colors = pieChartData.indices.map {
            Color.hsv(360.0f * it / count, 0.7f, 0.7f)
        }
        val slices = pieChartData.indices.map {
            val amount = if (checkedMap[it] ?: true) {
                pieChartData[it].second.toFloat()
            } else {
                0f
            }
            PieChartData.Slice(amount, colors[it])
        }

        val total = pieChartData
            .filterIndexed { index, pair ->
                checkedMap[index] ?: true
            }
            .sumOf { it.second }

        DialogWindow(
            onCloseRequest = { close() },
            state = dialogState
        ) {
            Row {
                PieChart(
                    pieChartData = PieChartData(slices),
                    modifier = Modifier.size(pieChartSize)
                )
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            onClick = {
                                expenseViewModel.pieChartPrevMonth()
                            }
                        ) {
                            Text("<")
                        }
                        Text(
                            text = labelMonthKey,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .wrapContentHeight()
                                .weight(1.0f)
                        )
                        Button(
                            onClick = {
                                expenseViewModel.pieChartNextMonth()
                            }
                        ) {
                            Text(">")
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.2f)
                        )
                        Button(
                            onClick = {
                                expenseViewModel.pieChartPrevMonth2()
                            }
                        ) {
                            Text("<")
                        }
                        Text(
                            text = labelMonthKey2,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .wrapContentHeight()
                                .weight(1.0f)
                        )
                        Button(
                            onClick = {
                                expenseViewModel.pieChartNextMonth2()
                            }
                        ) {
                            Text(">")
                        }
                    }
                    Row {
                        Text(
                            text = labelOverallCategory,
                            modifier = Modifier
                                .weight(3.25f),
                            color = Color.Black
                        )
                        Text(
                            text = total.toString(),
                            modifier = Modifier
                                .weight(1.0f),
                            color = Color.Black
                        )
                        if (monthCount > 1) {
                            Text(
                                text = (total / monthCount).toString(),
                                modifier = Modifier
                                    .weight(1.0f),
                                color = Color.Black
                            )
                        }
                        Text(
                            text =  "%.2f".format(100.0f) + " %",
                            modifier = Modifier
                                .weight(1.0f),
                            color = Color.Black
                        )
                    }
                    LazyColumn(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxHeight()
                    ) {
                        items(pieChartData.indices.toList()) { index ->
                            val amount = pieChartData[index].second
                            val percent = 100.0f * amount / total
                            val percentStr = "%.2f".format(percent) + " %"
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            )  {
                                Checkbox(
                                    checked = checkedMap[index] ?: true,
                                    onCheckedChange = {
                                        checkedMap[index] = it
                                    },
                                    modifier = Modifier
                                        .weight(0.25f)
                                )
                                Text(
                                    text = pieChartData[index].first.utf8toCP1251(),
                                    modifier = Modifier
                                        .weight(3.0f),
                                    color = colors[index]
                                )
                                Text(
                                    text = amount.toString(),
                                    modifier = Modifier
                                        .weight(1.0f),
                                    color = Color.Black
                                )
                                if (monthCount > 1) {
                                    Text(
                                        text = (amount / monthCount).toString(),
                                        modifier = Modifier
                                            .weight(1.0f),
                                        color = Color.Black
                                    )
                                }
                                Text(
                                    text = percentStr,
                                    modifier = Modifier
                                        .weight(1.0f),
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}