package jatx.expense.manager.domain.usecase

import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.xlsx.XlsxSaverFactory

class SaveXlsxUseCase(
    private val xlsxSaverFactory: XlsxSaverFactory
) {
    fun execute(expenseTable: ExpenseTable, xlsxPath: String) {
        val xlsxSaver = xlsxSaverFactory.newInstance(expenseTable, xlsxPath)
        xlsxSaver.saveXlsx()
    }
}