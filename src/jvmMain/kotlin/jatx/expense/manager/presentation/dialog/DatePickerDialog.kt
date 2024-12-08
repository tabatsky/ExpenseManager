package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.res.buttonCancelLabel
import jatx.expense.manager.res.buttonChooseLabel
import jatx.expense.manager.res.datePickerDialogHeight
import jatx.expense.manager.res.datePickerDialogWidth
import java.util.Date
import java.util.TimeZone

val timeZone = TimeZone.getDefault()
val offset = timeZone.rawOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogWrapper() {
    val expenseViewModel = appComponent.expenseViewModel

    val show by expenseViewModel.needShowDatePickerDialog.collectAsState()
    val date by expenseViewModel.datePickerDate.collectAsState()

    fun close() {
        expenseViewModel.showDatePickerDialog(false)
    }

    fun setDateAndClose(newDate: Date) {
        expenseViewModel.setDatePickerDate(newDate)
        close()
    }

    if (show) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.time + offset
        )
        val dialogState = rememberDialogState()
        dialogState.size = DpSize(datePickerDialogWidth, datePickerDialogHeight)
        DialogWindow(
            onCloseRequest = { close() },
            state = dialogState
        ) {
            Column(
                modifier = Modifier
                    .height(datePickerDialogHeight)
            ) {
                DatePicker(state = datePickerState)

                Row {
                    Button(onClick = {
                        val newDate = Date()
                        newDate.time = datePickerState.selectedDateMillis ?: date.time
                        setDateAndClose(newDate)
                    }) {
                        Text(buttonChooseLabel)
                    }

                    Divider(
                        modifier = Modifier
                            .weight(1.0f)
                    )

                    Button(onClick = {
                        close()
                    }) {
                        Text(buttonCancelLabel)
                    }
                }
            }
        }
    }
}