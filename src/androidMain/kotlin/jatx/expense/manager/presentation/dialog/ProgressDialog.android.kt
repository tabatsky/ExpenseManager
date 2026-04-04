package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import jatx.expense.manager.res.progressDialogSize

@Composable
actual fun ProgressDialog() {
    Dialog(
        onDismissRequest = { }
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