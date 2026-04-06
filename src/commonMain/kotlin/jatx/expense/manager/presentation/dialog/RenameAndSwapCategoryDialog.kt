package jatx.expense.manager.presentation.dialog

import androidx.compose.runtime.*
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.domain.models.RowKey

@Composable
fun RenameAndSwapCategoryDialogWrapper() {
    val expenseViewModel = appComponent.expenseViewModel

    val rowKeyToEdit by expenseViewModel.rowKeyToEdit.collectAsState()

    rowKeyToEdit?.let { rowKey ->
        val buttonUpEnabled = expenseViewModel.expenseTable.value?.let {
            it.isRegularRowKey(rowKey) && !it.isFirstRegularRowKeyForCardNameKey(rowKey)
        } ?: false
        val buttonDownEnabled = expenseViewModel.expenseTable.value?.let {
            it.isRegularRowKey(rowKey) && !it.isLastRegularRowKeyForCardNameKey(rowKey)
        } ?: false

        RenameAndSwapCategoryDialog(
            rowKey = rowKey,
            buttonUpEnabled = buttonUpEnabled,
            buttonDownEnabled = buttonDownEnabled,
            onDismiss = {
                expenseViewModel.showRenameCategoryDialog(null)
            },
            onSave = { newCategory ->
                expenseViewModel.renameCategoryAndReloadExpenseTable(newCategory, rowKey)
            },
            onUp = {
                expenseViewModel.swapRowKeysIntAndReloadExpenseTable(rowKey.rowKeyInt, rowKey.rowKeyInt - 1)
            },
            onDown = {
                expenseViewModel.swapRowKeysIntAndReloadExpenseTable(rowKey.rowKeyInt, rowKey.rowKeyInt + 1)
            }
        )
    }
}

@Composable
expect fun RenameAndSwapCategoryDialog(
    rowKey: RowKey,
    buttonUpEnabled: Boolean,
    buttonDownEnabled: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onUp: () -> Unit,
    onDown: () -> Unit
)