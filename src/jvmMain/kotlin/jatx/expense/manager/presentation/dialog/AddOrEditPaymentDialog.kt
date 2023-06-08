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
import jatx.expense.manager.res.buttonDeleteLabel
import jatx.expense.manager.res.buttonSaveLabel
import jatx.expense.manager.res.msgWrongNumberFormat


@Composable
fun EditPaymentDialogWrapper() {
    val expenseViewModel = Injector.expenseViewModel

    val currentPaymentEntry
            by expenseViewModel.currentPaymentEntry.collectAsState()

    currentPaymentEntry?.let { paymentEntry ->
        AddOrEditPaymentDialog(
            paymentEntry = paymentEntry,
            onDismiss = {
                expenseViewModel.showEditPaymentDialog(null)
            },
            onSave = {
                expenseViewModel.updatePaymentEntryToDB(it)
            },
            onDelete = {
                expenseViewModel.deletePaymentEntryFromDB(it)
            }
        )
    }
}

@Composable
fun AddPaymentDialogWrapper() {
    val expenseViewModel = Injector.expenseViewModel

    val newPaymentEntry
            by expenseViewModel.newPaymentEntry.collectAsState()

    newPaymentEntry?.let { paymentEntry ->
        AddOrEditPaymentDialog(
            paymentEntry = paymentEntry,
            onDismiss = {
                expenseViewModel.showAddPaymentDialog(false)
            },
            onSave = {
                expenseViewModel.insertPaymentEntryToDB(it)
            }
        )
    }
}

@Composable
private fun AddOrEditPaymentDialog(
    paymentEntry: PaymentEntry,
    onDismiss: () -> Unit,
    onSave: (PaymentEntry) -> Unit,
    onDelete: ((PaymentEntry) -> Unit)? = null
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
                enabled = (amount != 0),
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
            onDelete?.let {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        it.invoke(paymentEntry)
                        onDismiss()
                    }
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = buttonDeleteLabel
                    )
                }
            }
        }
    }
}