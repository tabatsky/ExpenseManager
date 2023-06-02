package jatx.expense.manager.presentation.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar

@Composable
fun FrameWindowScope.MainMenuBar(menuCallbacks: MenuCallbacks) = MenuBar {
    Menu("File") {
        Item("Load Xlsx") {
            menuCallbacks.onLoadXlsx?.invoke()
        }
    }
}

class MenuCallbacks {
    var onLoadXlsx: (() -> Unit)? = null
}