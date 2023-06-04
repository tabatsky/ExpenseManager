package jatx.expense.manager.presentation.view

import androidx.compose.foundation.*
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
import jatx.expense.manager.di.Injector
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.util.formattedForPaymentList
import jatx.expense.manager.res.yellowColor

@Composable
fun PaymentListView() {
    val expenseViewModel = Injector.expenseViewModel

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
    val expenseViewModel = Injector.expenseViewModel

    Column(
        modifier = Modifier
            .border(BorderStroke(1.dp, Color.Black))
            .background(yellowColor)
            .fillMaxWidth()
            .clickable {
                expenseViewModel.showEditPaymentDialog(paymentEntry)
            }
    ) {
        Text(text = paymentEntry.amount.toString())
        Text(text = paymentEntry.comment)
        Text(text = paymentEntry.date.formattedForPaymentList)
    }
}