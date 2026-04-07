package jatx.expense.manager.presentation.menu

import androidx.compose.foundation.background
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.res.buttonFontSize
import jatx.expense.manager.res.menuByMonthChart
import jatx.expense.manager.res.menuPieChart
import jatx.expense.manager.res.menuPieChartByComment

@Composable
actual fun DropdownMenuWrapper(menuCallbacks: MenuCallbacks) {
    val expenseViewModel = appComponent.expenseViewModel

    val show by expenseViewModel.needShowDropdownMenu.collectAsState()

    fun close() {
        expenseViewModel.showDropdownMenu(false)
    }

    DropdownMenu(
        expanded = show,
        modifier = Modifier
            .background(Color.White),
        onDismissRequest = {
            close()
        }
    ) {
        DropdownMenuItem(onClick = {
            menuCallbacks.onShowPieChart?.invoke()
        }) {
            Text(
                text = menuPieChart,
                color = Color.Black,
                fontSize = buttonFontSize
            )
        }
        DropdownMenuItem(onClick = {
            menuCallbacks.onShowPieChartByComment?.invoke()
        }) {
            Text(
                text = menuPieChartByComment,
                color = Color.Black,
                fontSize = buttonFontSize
            )
        }
        DropdownMenuItem(onClick = {
            menuCallbacks.onShowByMonthChart?.invoke()
        }) {
            Text(
                text = menuByMonthChart,
                color = Color.Black,
                fontSize = buttonFontSize
            )
        }
    }
}