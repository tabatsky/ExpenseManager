package jatx.expense.manager.presentation.dialog

import androidx.compose.runtime.*
import java.util.TimeZone

val timeZone = TimeZone.getDefault()
val offset = timeZone.rawOffset

@Composable
expect fun DatePickerDialogWrapper()