package jatx.expense.manager

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import jatx.expense.manager.data.db.DatabaseDriverFactory
import jatx.expense.manager.di.Injector
import jatx.expense.manager.presentation.dialog.XlsxChooserDialogWrapper
import jatx.expense.manager.presentation.view.MainScreen
import jatx.expense.manager.presentation.menu.MainMenuBar

fun main() {
    application {
        val factory = DatabaseDriverFactory()
        Injector.init(factory, rememberCoroutineScope())

        Window(onCloseRequest = ::exitApplication) {
            window.placement = WindowPlacement.Maximized
            XlsxChooserDialogWrapper()
            MainMenuBar(Injector.menuCallbacks)
            MaterialTheme {
                MainScreen(Injector.expenseViewModel)
            }
        }
    }
}
