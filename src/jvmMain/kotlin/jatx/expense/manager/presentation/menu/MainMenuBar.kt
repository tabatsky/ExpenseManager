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
        Item(menuStatisticsByCommentLabel) {
            menuCallbacks.onShowStatisticsByComment?.invoke()
        }
        Item(menuStatisticsByCategoryLabel) {
            menuCallbacks.onShowStatisticsByCategory?.invoke()
        }
        Item(menuPieChart) {
            menuCallbacks.onShowPieChart?.invoke()
        }
        Item(menuPieChartByComment) {
            menuCallbacks.onShowPieChartByComment?.invoke()
        }
        Item(menuPieChartByCommentMinus) {
            menuCallbacks.onShowPieChartByCommentMinus?.invoke()
        }
        Item(menuByMonthChart) {
            menuCallbacks.onShowByMonthChart?.invoke()
        }
    }
}
