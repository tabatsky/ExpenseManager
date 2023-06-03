package jatx.expense.manager.presentation.dialog

import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowScope
import jatx.expense.manager.data.xlsx.theFolderPath
import jatx.expense.manager.di.Injector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun WindowScope.XlsxChooserDialogWrapper() {
    val expenseViewModel = Injector.expenseViewModel

    val needShowXlsxChooserDialog
            by expenseViewModel.needShowXlsxChooserDialog.collectAsState()
    val xlsxChooserDialogShowCounter
            by expenseViewModel.xlsxChooserDialogShowCounter.collectAsState()

    if (needShowXlsxChooserDialog) {
        XlsxChooserDialog(
            coroutineScope = rememberCoroutineScope(),
            onFileOpened = {
                expenseViewModel.loadXlsxToDB(it.absolutePath)
            },
            onDispose = {
                expenseViewModel.showXlsxChooserDialog(false)
            },
            showCounter = xlsxChooserDialogShowCounter
        )
    }
}

@Composable
private fun WindowScope.XlsxChooserDialog(
    coroutineScope: CoroutineScope,
    onFileOpened: (File) -> Unit,
    onDispose: () -> Unit,
    showCounter: Int
) {
    DisposableEffect(showCounter) {
        val job = coroutineScope.launch {
            val fileChooser = JFileChooser()
            fileChooser.dialogTitle = "Open XLSX"
            File(theFolderPath)
                .takeIf { it.exists() }
                .let {
                    fileChooser.currentDirectory = it
                }
            fileChooser.isMultiSelectionEnabled = false
            listOf(
                FileNameExtensionFilter("Excel Table", "xlsx")
            ).forEach {
                fileChooser.addChoosableFileFilter(it)
            }

            val result = fileChooser.showOpenDialog(window)
            when (result) {
                JFileChooser.APPROVE_OPTION -> {
                    onFileOpened(fileChooser.selectedFile)
                }
                else -> {}
            }
        }

        onDispose {
            job.cancel()
            onDispose()
        }
    }
}
