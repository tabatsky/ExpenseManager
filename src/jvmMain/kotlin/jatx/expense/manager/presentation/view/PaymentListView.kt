package jatx.expense.manager.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.util.formattedForPaymentList
import jatx.expense.manager.presentation.res.yellowColor
import jatx.expense.manager.presentation.viewmodel.ExpenseViewModel

@Composable
fun PaymentList(expenseViewModel: ExpenseViewModel) {
    val expenseEntry by expenseViewModel.currentExpenseEntry.collectAsState()

    expenseEntry?.let {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(it.payments.reversed()) { paymentEntry ->
                PaymentItem(paymentEntry)
            }
        }
    }
}

@Composable
fun PaymentItem(paymentEntry: PaymentEntry) {
    Column(
        modifier = Modifier
            .border(BorderStroke(1.dp, Color.Black))
            .background(yellowColor)
            .fillMaxWidth()
    ) {
        Text(text = paymentEntry.amount.toString())
        Text(text = paymentEntry.comment)
        Text(text = paymentEntry.date.formattedForPaymentList)
    }
}