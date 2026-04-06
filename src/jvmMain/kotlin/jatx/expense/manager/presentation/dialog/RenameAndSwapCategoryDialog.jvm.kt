package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogWindow
import jatx.expense.manager.domain.models.RowKey
import jatx.expense.manager.domain.util.cp1251toUTF8
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.res.buttonCancelLabel
import jatx.expense.manager.res.buttonDownLabel
import jatx.expense.manager.res.buttonSaveLabel
import jatx.expense.manager.res.buttonUpLabel

@Composable
actual fun RenameAndSwapCategoryDialog(
    rowKey: RowKey,
    buttonUpEnabled: Boolean,
    buttonDownEnabled: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onUp: () -> Unit,
    onDown: () -> Unit
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
                }, modifier = Modifier
                    .weight(1.0f)
                ) {
                    Text(buttonSaveLabel)
                }

                Divider(
                    modifier = Modifier
                        .weight(1.0f)
                )

                Button(onClick = {
                    onDismiss()
                }, modifier = Modifier
                    .weight(1.0f)
                ) {
                    Text(buttonCancelLabel)
                }
            }

            Row {
                Button(enabled = buttonUpEnabled,
                    onClick = {
                        onUp()
                        onDismiss()
                    }, modifier = Modifier
                        .weight(1.0f)
                ) {
                    Text(buttonUpLabel)
                }

                Divider(
                    modifier = Modifier
                        .weight(1.0f)
                )

                Button(enabled = buttonDownEnabled,
                    onClick = {
                        onDown()
                        onDismiss()
                    }, modifier = Modifier
                        .weight(1.0f)
                ) {
                    Text(buttonDownLabel)
                }
            }
        }
    }
}