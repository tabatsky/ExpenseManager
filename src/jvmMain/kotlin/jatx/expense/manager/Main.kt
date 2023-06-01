package jatx.expense.manager

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import jatx.expense.manager.data.db.DatabaseDriverFactory
import jatx.expense.manager.data.filesystem.theXlsPath
import jatx.expense.manager.di.Injector
import jatx.expense.manager.presentation.view.MainScreen

fun main() {
    application {
        val factory = DatabaseDriverFactory()
        Injector.init(factory, rememberCoroutineScope())

        Injector.expenseViewModel.loadXlsxToDB(theXlsPath)
        Injector.expenseViewModel.loadExpenseTableFromDB()

        Window(onCloseRequest = ::exitApplication) {
            MaterialTheme {
                MainScreen(Injector.expenseViewModel)
            }
        }
    }
}
