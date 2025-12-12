package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import jatx.expense.manager.domain.util.cp1251toUTF8
import jatx.expense.manager.domain.util.dateFromMonthKey
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.res.*
import java.util.*

@Composable
fun PieChartByCommentDialogWrapper() {
    val expenseViewModel = appComponent.expenseViewModel

    val show by expenseViewModel.needShowPieChartByCommentDialog.collectAsState()

    fun close() {
        expenseViewModel.showPieChartByCommentDialog(false)
    }

    if (show) {
        val dialogState = rememberDialogState()
        dialogState.size = DpSize(pieChartDialogWidth, pieChartDialogHeight)

        val monthKey by expenseViewModel.pieChartMonthKey.collectAsState()
        val monthKey2 by expenseViewModel.pieChartMonthKey2.collectAsState()
        val filter by expenseViewModel.pieChartFilter.collectAsState()

        val labelMonthKey = if (monthKey <= Date().monthKey)
            monthKey.dateFromMonthKey.formattedMonthAndYear
        else
            labelOverallTime

        val labelMonthKey2 = monthKey2?.dateFromMonthKey?.formattedMonthAndYear ?: labelNotSet

        val pieChartData = if (monthKey <= Date().monthKey)
            expenseViewModel.pieChartDataByComment(monthKey.dateFromMonthKey, monthKey2?.dateFromMonthKey, filter)
        else
            expenseViewModel.overallPieChartDataByComment(filter)

        val count = pieChartData.size
        val colors = pieChartData.indices.map {
            Color.hsv(360.0f * it / count, 0.7f, 0.7f)
        }
        val slices = pieChartData.indices.map {
            PieChartData.Slice(pieChartData[it].second.toFloat(), colors[it])
        }

        val total = pieChartData.sumOf { it.second }

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
                    )  {
                        var filterTmp by remember { mutableStateOf(filter.utf8toCP1251()) }

                        TextField(
                            value = filterTmp,
                            onValueChange = {
                                filterTmp = it
                                expenseViewModel.pieChartUpdateFilter(filterTmp.cp1251toUTF8())
                                 },
                            label = { Text(labelPieChartFilter) },
                            singleLine = true
                        )
                    }
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
                                .weight(3.0f),
                            color = Color.Black
                        )
                        Text(
                            text = total.toString(),
                            modifier = Modifier
                                .weight(1.0f),
                            color = Color.Black
                        )
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