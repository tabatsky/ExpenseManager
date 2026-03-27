package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.res.progressDialogSize

@Composable
fun ProgressDialogWrapper() {
    val expenseViewModel = appComponent.expenseViewModel

    val visible by expenseViewModel.needShowProgressDialog.collectAsState()

    if (visible) {
        ProgressDialog()
    }
}

@Composable
private fun ProgressDialog() {
    val dialogState = rememberDialogState()
    dialogState.size = DpSize(progressDialogSize, progressDialogSize)
    DialogWindow(
        onCloseRequest = { },
        state = dialogState
    ) {
        Row{
            Spacer(
                modifier = Modifier
                    .weight(1.0f)
            )
            Column {
                Spacer(
                    modifier = Modifier
                        .weight(1.0f)
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(progressDialogSize * 0.7f)
                )
                Spacer(
                    modifier = Modifier
                        .weight(1.0f)
                )
            }
            Spacer(
                modifier = Modifier
                    .weight(1.0f)
            )
        }
    }
}