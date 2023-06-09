package jatx.expense.manager.presentation.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import jatx.expense.manager.res.menuFileLabel
import jatx.expense.manager.res.menuLoadXlsxLabel
import jatx.expense.manager.res.menuSaveXlsxLabel

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
}

class MenuCallbacks {
    var onLoadXlsx: (() -> Unit)? = null
    var onSaveXlsx: (() -> Unit)? = null
}