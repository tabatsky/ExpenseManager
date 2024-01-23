package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.window.DialogWindow
import jatx.expense.manager.di.Injector
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.res.*
import java.text.SimpleDateFormat
import kotlin.math.absoluteValue

val sdf = SimpleDateFormat("dd.MM.yyyy")

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
    val expenseViewModel = Injector.expenseViewModel

    var amount by remember { mutableStateOf(paymentEntry.amount) }
    var comment by remember { mutableStateOf(paymentEntry.comment) }

    val enabled = amount != 0
    val saveLabel = if (enabled) buttonSaveLabel else buttonSaveZeroLabel

    LaunchedEffect(Unit) {
        expenseViewModel.setDatePickerDate(paymentEntry.date)
    }

    val date by expenseViewModel.datePickerDate.collectAsState()

    DialogWindow(onCloseRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val commentFocusRequester = remember { FocusRequester() }
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = amount.toString(),
                onValueChange = {
                    val tabCount = it.count { it == '\t' }
                    if (tabCount > 0) {
                        commentFocusRequester.requestFocus()
                    } else {
                        try {
                            val minusCount = it.count { it == '-' }
                            val sign = 1 - 2 * (minusCount % 2)
                            val filteredString = it.replace("-", "")
                            val absoluteAmount = filteredString.toInt().absoluteValue
                            amount = sign * absoluteAmount
                        } catch (e: NumberFormatException) {
                            println(msgWrongNumberFormat)
                        }
                    }
                }
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(commentFocusRequester),
                value = comment,
                onValueChange = {
                    comment = it
                }
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = true,
                onClick = {
                    expenseViewModel.showDatePickerDialog(true)
                }
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = sdf.format(date)
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = enabled,
                onClick = {
                    onSave(paymentEntry.copy(amount = amount, comment = comment, date = date))
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