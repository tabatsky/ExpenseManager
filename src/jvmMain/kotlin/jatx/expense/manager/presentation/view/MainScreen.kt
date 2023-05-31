package jatx.expense.manager.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jatx.expense.manager.presentation.viewmodel.ExpenseViewModel

@Composable
fun MainScreen(expenseViewModel: ExpenseViewModel) {
    Row {
        Box(modifier = Modifier
            .fillMaxHeight()
            .weight(0.75f)) {
            ExpenseTable(expenseViewModel)
        }
        Box(modifier = Modifier
            .fillMaxHeight()
            .width(20.dp)
        )
        Box(modifier = Modifier
            .fillMaxHeight()
            .weight(0.25f)) {
            PaymentList(expenseViewModel)
        }
    }
}