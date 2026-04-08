package jatx.expense.manager.presentation.menu

class MenuCallbacks {
    var onLoadXlsx: (() -> Unit)? = null
    var onSaveXlsx: (() -> Unit)? = null
    var onShowStatisticsByComment: (() -> Unit)? = null
    var onShowStatisticsByCategory: (() -> Unit)? = null
    var onShowPieChart: (() -> Unit)? = null
    var onShowPieChartByComment: (() -> Unit)? = null
    var onShowPieChartByCommentMinus: (() -> Unit)? = null
    var onShowByMonthChart: (() -> Unit)? = null
    var onAppExit: (() -> Unit)? = null
}