package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.window.DialogWindow
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.res.buttonDeleteLabel
import jatx.expense.manager.res.buttonSaveLabel
import jatx.expense.manager.res.buttonSaveZeroLabel
import jatx.expense.manager.res.msgWrongNumberFormat
import kotlin.math.absoluteValue

@Composable
actual fun AddOrEditPaymentDialog(
    paymentEntry: PaymentEntry,
    onDismiss: () -> Unit,
    onSave: (PaymentEntry) -> Unit,
    onDelete: (() -> Unit)?
) {
    val expenseViewModel = appComponent.expenseViewModel

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
                        } catch (_: NumberFormatException) {
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