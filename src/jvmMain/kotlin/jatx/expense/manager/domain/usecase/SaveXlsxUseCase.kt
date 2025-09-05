package jatx.expense.manager.domain.usecase

import jatx.expense.manager.di.AppScope
import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.xlsx.XlsxSaverFactory
import me.tatarka.inject.annotations.Inject

//const val theDefaultXlsxPath = "C:\\Users\\User\\Desktop\\Expense\\траты.xlsx"
const val theDefaultXlsxPath = "траты.xlsx"

@AppScope
@Inject
class SaveXlsxUseCase(
    private val xlsxSaverFactory: XlsxSaverFactory
) {
    fun execute(expenseTable: ExpenseTable, xlsxPath: String) {
        if (expenseTable.cellCount > 0) {
            val xlsxSaver = xlsxSaverFactory.newInstance(expenseTable, xlsxPath)
            xlsxSaver.saveXlsx()
        }
    }
}