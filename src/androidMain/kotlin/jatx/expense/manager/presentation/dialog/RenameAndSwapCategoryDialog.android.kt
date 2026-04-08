package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import jatx.expense.manager.domain.models.RowKey
import jatx.expense.manager.domain.util.cp1251toUTF8
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.res.buttonCancelLabel
import jatx.expense.manager.res.buttonDownLabel
import jatx.expense.manager.res.buttonFontSize
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

    Dialog(onDismissRequest = { onDismiss() }) {
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = categoryCP1251,
                onValueChange = {
                    categoryCP1251 = it
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        keyboardController?.hide()
                    }
                )
            )

            Row {
                Button(onClick = {
                    onSave(categoryCP1251.cp1251toUTF8())
                    onDismiss()
                }, modifier = Modifier
                    .weight(1.0f)
                ) {
                    Text(
                        text = buttonSaveLabel,
                        textAlign = TextAlign.Center,
                        fontSize = buttonFontSize
                    )
                }

                Spacer(
                    modifier = Modifier
                        .weight(0.05f)
                )

                Button(onClick = {
                    onDismiss()
                }, modifier = Modifier
                    .weight(1.0f)
                ) {
                    Text(
                        text = buttonCancelLabel,
                        textAlign = TextAlign.Center,
                        fontSize = buttonFontSize
                    )
                }

                Spacer(
                    modifier = Modifier
                        .weight(0.05f)
                )

                Button(enabled = buttonUpEnabled,
                    onClick = {
                        onUp()
                        onDismiss()
                    }, modifier = Modifier
                        .weight(1.0f)
                ) {
                    Text(
                        text = buttonUpLabel,
                        textAlign = TextAlign.Center,
                        fontSize = buttonFontSize
                    )
                }

                Spacer(
                    modifier = Modifier
                        .weight(0.05f)
                )

                Button(enabled = buttonDownEnabled,
                    onClick = {
                        onDown()
                        onDismiss()
                    }, modifier = Modifier
                        .weight(1.0f)
                ) {
                    Text(
                        text = buttonDownLabel,
                        textAlign = TextAlign.Center,
                        fontSize = buttonFontSize
                    )
                }
            }
        }
    }
}

