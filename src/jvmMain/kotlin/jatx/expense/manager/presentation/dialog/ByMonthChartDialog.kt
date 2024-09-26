package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import jatx.expense.manager.di.Injector
import jatx.expense.manager.res.*

@Composable
fun ByMonthChartDialogWrapper() {
    val expenseViewModel = Injector.expenseViewModel

    val show by expenseViewModel.needShowByMonthChartDialog.collectAsState()

    fun close() {
        expenseViewModel.showByMonthChartDialog(false)
    }

    if (show) {
        val dialogState = rememberDialogState()
        dialogState.size = DpSize(pieChartDialogWidth, pieChartDialogHeight)

        val byMonthData = expenseViewModel.byMonthData()

        DialogWindow(
            onCloseRequest = { close() },
            state = dialogState
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
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