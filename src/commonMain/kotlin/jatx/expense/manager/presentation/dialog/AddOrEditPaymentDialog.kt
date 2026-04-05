package jatx.expense.manager.presentation.dialog

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.res.*
import java.text.SimpleDateFormat

val sdf = SimpleDateFormat("dd.MM.yyyy")

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditPaymentDialogWrapper() {
    val expenseViewModel = appComponent.expenseViewModel

    val currentPaymentEntry by expenseViewModel.currentPaymentEntry.collectAsState()
    val showEditPaymentDialog by expenseViewModel.needShowEditPaymentDialog.collectAsState()

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
    val expenseViewModel = appComponent.expenseViewModel

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
expect fun AddOrEditPaymentDialog(
    paymentEntry: PaymentEntry,
    onDismiss: () -> Unit,
    onSave: (PaymentEntry) -> Unit,
    onDelete: (() -> Unit)? = null
)