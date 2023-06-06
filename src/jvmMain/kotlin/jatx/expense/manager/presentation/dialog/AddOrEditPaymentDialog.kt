package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import jatx.expense.manager.di.Injector
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.res.buttonSaveLabel
import jatx.expense.manager.res.msgWrongNumberFormat


@Composable
fun EditPaymentDialogWrapper() {
    val expenseViewModel = Injector.expenseViewModel

    val currentPaymentEntry
            by expenseViewModel.currentPaymentEntry.collectAsState()

    currentPaymentEntry?.let {
        AddOrEditPaymentDialog(
            paymentEntry = it,
            onDismiss = {
                expenseViewModel.showEditPaymentDialog(null)
            },
            onSave = {
                expenseViewModel.updateExpenseEntryToDB(it)
            }
        )
    }
}

@Composable
fun AddPaymentDialogWrapper() {
    val expenseViewModel = Injector.expenseViewModel

    val newPaymentEntry
            by expenseViewModel.newPaymentEntry.collectAsState()

    newPaymentEntry?.let {
        AddOrEditPaymentDialog(
            paymentEntry = it,
            onDismiss = {
                expenseViewModel.showAddPaymentDialog(false)
            },
            onSave = {
                expenseViewModel.insertExpenseEntryToDB(it)
            }
        )
    }
}

@Composable
private fun AddOrEditPaymentDialog(
    paymentEntry: PaymentEntry,
    onDismiss: () -> Unit,
    onSave: (PaymentEntry) -> Unit
) {
    var amount by remember { mutableStateOf(paymentEntry.amount) }
    var comment by remember { mutableStateOf(paymentEntry.comment) }

    Dialog(onCloseRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = amount.toString(),
                onValueChange = {
                    try {
                        val tmpAmount = it.toInt()
                        amount = tmpAmount
                    } catch (e: NumberFormatException) {
                        println(msgWrongNumberFormat)
                    }
                }
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = comment,
                onValueChange = {
                    comment = it
                }
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    onSave(paymentEntry.copy(amount = amount, comment = comment))
                    onDismiss()
                }
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = buttonSaveLabel
                )
            }
        }
    }
}