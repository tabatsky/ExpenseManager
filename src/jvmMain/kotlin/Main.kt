import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import jatx.expense.manager.data.theXlsPath
import jatx.expense.manager.presentation.view.ExpenseTable
import jatx.expense.manager.presentation.viewmodel.ExpenseViewModel

fun main() {
    val expenseViewModel = ExpenseViewModel()
    expenseViewModel.loadXlsx(theXlsPath)

    application {
        Window(onCloseRequest = ::exitApplication) {
            MaterialTheme {
                ExpenseTable(expenseViewModel)
            }
        }
    }
}
