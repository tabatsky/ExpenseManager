package jatx.expense.manager.res

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jatx.expense.manager.platform.isAndroid

val firstCellWidth = 72.dp
val secondCellWidth = 120.dp
val cellWidth = 52.dp
val cellHeight = 24.dp
val paymentCellHeight = 72.dp

val datePickerDialogWidth = if (isAndroid) 300.dp else 400.dp
val datePickerDialogHeight = if (isAndroid) 300.dp else 600.dp

val pieChartDialogWidth = if (isAndroid) 600.dp else 1200.dp
val pieChartDialogHeight = if (isAndroid) 300.dp else 600.dp
val pieChartSize = 600.dp

val progressDialogSize = 400.dp


val buttonHeight = 36.dp

val borderWidth = 1.dp

val commonFontSize = 10.sp
val buttonFontSize = if (isAndroid) 10.sp else 16.sp

val barChartLabelTextSize = if (isAndroid) 4.sp else 12.sp
