package jatx.expense.manager.presentation.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import jatx.expense.manager.di.appComponent

@Composable
fun ProgressDialogWrapper() {
    val expenseViewModel = appComponent.expenseViewModel

    val visible by expenseViewModel.needShowProgressDialog.collectAsState()

    if (visible) {
        ProgressDialog()
    }
}

@Composable
expect fun ProgressDialog()