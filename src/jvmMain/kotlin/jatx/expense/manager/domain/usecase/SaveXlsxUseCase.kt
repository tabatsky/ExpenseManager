package jatx.expense.manager.domain.usecase

import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.xlsx.XlsxSaverFactory

const val theDefaultXlsxPath = "C:\\Users\\User\\Desktop\\Expense\\траты.xlsx"

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