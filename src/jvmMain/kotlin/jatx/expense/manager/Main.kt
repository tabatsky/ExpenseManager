package jatx.expense.manager

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import jatx.expense.manager.data.firebase.defaultFirebaseConfig
import jatx.expense.manager.data.firebase.readFirebaseAuthDataFromFile
import jatx.expense.manager.di.AppComponent
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.di.create
import jatx.expense.manager.presentation.menu.MainMenuBar
import jatx.expense.manager.presentation.dialog.XlsxChooserDialogWrapper
import jatx.expense.manager.presentation.view.MainScreen

fun main() {
    application {
        val coroutineScope = rememberCoroutineScope()

        appComponent = AppComponent::class.create(coroutineScope)

        val firebaseConfig = defaultFirebaseConfig()
        val firebaseAuthData = readFirebaseAuthDataFromFile()

        appComponent.expenseViewModel.onAppStart(firebaseConfig, firebaseAuthData)

        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

        Window(
            onCloseRequest = {
                appComponent.expenseViewModel.onAppExit {
                    exitApplication()
                }
            },
            state = windowState
        ) {
            XlsxChooserDialogWrapper()
            MainMenuBar(appComponent.menuCallbacks)
            MaterialTheme {
                MainScreen()
            }
        }
    }
}
