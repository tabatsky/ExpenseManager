package jatx.expense.manager.presentation.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import jatx.expense.manager.res.*

@Composable
fun FrameWindowScope.MainMenuBar(menuCallbacks: MenuCallbacks) = MenuBar {
    Menu(menuFileLabel) {
        Item(menuLoadXlsxLabel) {
            menuCallbacks.onLoadXlsx?.invoke()
        }
        Item(menuSaveXlsxLabel) {
            menuCallbacks.onSaveXlsx?.invoke()
        }
    }
    Menu(menuFunctionLabel) {
        Item(menuStatisticsLabel) {
            menuCallbacks.onShowStatistics?.invoke()
        }
    }
}

class MenuCallbacks {
    var onLoadXlsx: (() -> Unit)? = null
    var onSaveXlsx: (() -> Unit)? = null
    var onShowStatistics: (() -> Unit)? = null
}