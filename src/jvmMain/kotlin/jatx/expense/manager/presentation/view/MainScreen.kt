package jatx.expense.manager.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jatx.expense.manager.presentation.dialog.AddPaymentDialogWrapper
import jatx.expense.manager.presentation.dialog.DatePickerDialogWrapper
import jatx.expense.manager.presentation.dialog.EditPaymentDialogWrapper

@Composable
fun MainScreen() {
    Box {
        EditPaymentDialogWrapper()
        AddPaymentDialogWrapper()
        DatePickerDialogWrapper()

        Row {
            Box(modifier = Modifier
                .fillMaxHeight()
                .weight(0.75f)) {
                ExpenseTable()
            }
            Box(modifier = Modifier
                .fillMaxHeight()
                .width(20.dp)
            )
            Box(modifier = Modifier
                .fillMaxHeight()
                .weight(0.25f)) {
                PaymentListView()
            }
        }
    }
}