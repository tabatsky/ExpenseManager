package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogWindow
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.domain.models.RowKey
import jatx.expense.manager.domain.util.cp1251toUTF8
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.res.buttonCancelLabel
import jatx.expense.manager.res.buttonSaveLabel

@Composable
fun RenameCategoryDialogWrapper() {
    val expenseViewModel = appComponent.expenseViewModel

    val rowKeyToEdit by expenseViewModel.rowKeyToEdit.collectAsState()

    rowKeyToEdit?.let { rowKey ->
        RenameCategoryDialog(
            rowKey = rowKey,
            onDismiss = {
                expenseViewModel.showRenameCategoryDialog(null)
            },
            onSave = { newCategory ->
                expenseViewModel.renameCategoryAndReloadExpenseTable(newCategory, rowKey)
            }
        )
    }
}

@Composable
private fun RenameCategoryDialog(
    rowKey: RowKey,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var categoryCP1251 by remember { mutableStateOf(rowKey.category.utf8toCP1251()) }

    DialogWindow(onCloseRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = categoryCP1251,
                onValueChange = {
                    categoryCP1251 = it
                }
            )

            Row {
                Button(onClick = {
                    onSave(categoryCP1251.cp1251toUTF8())
                    onDismiss()
                }) {
                    Text(buttonSaveLabel)
                }

                Divider(
                    modifier = Modifier
                        .weight(1.0f)
                )

                Button(onClick = {
                    onDismiss()
                }) {
                    Text(buttonCancelLabel)
                }
            }
        }
    }
}