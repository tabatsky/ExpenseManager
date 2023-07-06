package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import jatx.expense.manager.di.Injector
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.res.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditPaymentDialogWrapper() {
    val expenseViewModel = Injector.expenseViewModel

    val currentPaymentEntry by expenseViewModel.currentPaymentEntry.collectAsState()
    val showEditPaymentDialog by expenseViewModel.showEditPaymentDialog.collectAsState()

    currentPaymentEntry?.let { paymentEntry ->
        var showConfirmation by rememberSaveable { mutableStateOf(false) }

        if (showEditPaymentDialog) {
            AddOrEditPaymentDialog(
                paymentEntry = paymentEntry,
                onDismiss = {
                    expenseViewModel.showEditPaymentDialog(paymentEntry, false)
                },
                onSave = {
                    expenseViewModel.updatePaymentEntryAtDBAndReloadExpenseTable(it)
                },
                onDelete = {
                    showConfirmation = true
                }
            )
        }

        if (showConfirmation) {
            AlertDialog(
                onDismissRequest = {
                    showConfirmation = false
                },
                title = {
                    Text(text = titleDeletionConfirmationDialog)
                },
                text = {
                    Text(text = textDeletionConfirmationDialog)
                },
                confirmButton = {
                    Button(onClick = {
                        expenseViewModel.deletePaymentEntryFromDBAndReloadExpenseTable(paymentEntry)
                        showConfirmation = false
                    }) {
                        Text(text = buttonYesLabel)
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showConfirmation = false
                    }) {
                        Text(text = buttonNoLabel)
                    }
                }
            )
        }
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
                expenseViewModel.insertPaymentEntryIntoDBAndReloadExpenseTable(it)
            }
        )
    }
}

@Composable
private fun AddOrEditPaymentDialog(
    paymentEntry: PaymentEntry,
    onDismiss: () -> Unit,
    onSave: (PaymentEntry) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var amount by remember { mutableStateOf(paymentEntry.amount) }
    var comment by remember { mutableStateOf(paymentEntry.comment) }

    val enabled = amount != 0
    val saveLabel = if (enabled) buttonSaveLabel else buttonSaveZeroLabel

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
                enabled = enabled,
                onClick = {
                    onSave(paymentEntry.copy(amount = amount, comment = comment))
                    onDismiss()
                }
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = saveLabel
                )
            }
            onDelete?.let {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        it.invoke()
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