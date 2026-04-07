package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.domain.util.cp1251toUTF8
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.res.*

@Composable
actual fun ByMonthChartDialogWrapper() {
    val expenseViewModel = appComponent.expenseViewModel

    val show by expenseViewModel.needShowByMonthChartDialog.collectAsState()

    fun close() {
        expenseViewModel.showByMonthChartDialog(false)
    }

    if (show) {
        val dialogState = rememberDialogState()
        dialogState.size = DpSize(pieChartDialogWidth, pieChartDialogHeight)

        val filter by expenseViewModel.pieChartFilter.collectAsState()
        val byMonthData by expenseViewModel.byMonthData(filter).collectAsState(listOf())

        DialogWindow(
            onCloseRequest = { close() },
            state = dialogState
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
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
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxWidth(),
                ) {
                    BarChart(
                        BarChartData(byMonthData),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 40.dp, top = 120.dp, end = 40.dp, bottom = 40.dp)
                    )
                }
            }
        }
    }
}