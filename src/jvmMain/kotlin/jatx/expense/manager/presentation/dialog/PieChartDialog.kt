package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import jatx.expense.manager.di.Injector
import jatx.expense.manager.domain.util.dateFromMonthKey
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.res.labelOverallTime
import jatx.expense.manager.res.pieChartDialogHeight
import jatx.expense.manager.res.pieChartDialogWidth
import jatx.expense.manager.res.pieChartSize
import java.util.*

@Composable
fun PieChartDialogWrapper() {
    val expenseViewModel = Injector.expenseViewModel

    val show by expenseViewModel.needShowPieChartDialog.collectAsState()

    fun close() {
        expenseViewModel.showPieChartDialog(false)
    }

    if (show) {
        val dialogState = rememberDialogState()
        dialogState.size = DpSize(pieChartDialogWidth, pieChartDialogHeight)

        val monthKey by expenseViewModel.pieChartMonthKey.collectAsState()

        val labelMonthKey = if (monthKey <= Date().monthKey)
            monthKey.dateFromMonthKey.formattedMonthAndYear
        else
            labelOverallTime

        val pieChartData = if (monthKey <= Date().monthKey)
            expenseViewModel.pieChartData(monthKey.dateFromMonthKey)
        else
            expenseViewModel.overallPieChartData()

        val count = pieChartData.size
        val colors = pieChartData.indices.map {
            Color.hsv(360.0f * it / count, 0.7f, 1.0f)
        }
        val slices = pieChartData.indices.map {
            PieChartData.Slice(pieChartData[it].second.toFloat(), colors[it])
        }

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
                    Row {
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
                    }
                    LazyColumn(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxHeight()
                    ) {
                        val total = pieChartData.sumOf { it.second }
                        items(pieChartData.indices.toList()) {
                            val amount = pieChartData[it].second
                            val percent = 100.0f * amount / total
                            val percentStr = "%.2f".format(percent) + " %"
                            Row {
                                Text(
                                    text = pieChartData[it].first.utf8toCP1251(),
                                    modifier = Modifier
                                        .weight(3.0f),
                                    color = colors[it]
                                )
                                Text(
                                    text = amount.toString(),
                                    modifier = Modifier
                                        .weight(1.0f),
                                    color = Color.Black
                                )
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