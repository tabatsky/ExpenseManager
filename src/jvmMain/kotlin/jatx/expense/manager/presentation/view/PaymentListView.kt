package jatx.expense.manager.presentation.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jatx.expense.manager.di.Injector
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.util.formattedForPaymentList
import jatx.expense.manager.res.buttonAddLabel
import jatx.expense.manager.res.buttonFontSize
import jatx.expense.manager.res.buttonHeight
import jatx.expense.manager.res.yellowColor

@Composable
fun PaymentListView() {
    val expenseViewModel = Injector.expenseViewModel

    val expenseEntry by expenseViewModel.currentExpenseEntry.collectAsState()

    expenseEntry?.let {
        Column {
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)) {
                LazyColumn {
                    items(it.payments.reversed()) { paymentEntry ->
                        PaymentItem(paymentEntry)
                    }
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()) {
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight),
                    onClick = {
                        expenseViewModel.showAddPaymentDialog(true)
                    }) {
                    Text(
                        text = buttonAddLabel,
                        textAlign = TextAlign.Center,
                        fontSize = buttonFontSize
                    )
                }
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