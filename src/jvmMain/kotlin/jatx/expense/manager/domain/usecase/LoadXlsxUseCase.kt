package jatx.expense.manager.domain.usecase

import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.xlsx.XlsxParserFactory

class LoadXlsxUseCase(
    private val xlsxParserFactory: XlsxParserFactory
) { fun execute(xlsxPath: String): ExpenseTable = run {
        val xlsxParser = xlsxParserFactory.newInstance(xlsxPath)
        val expenseTable = xlsxParser.parseXlsx()
        expenseTable
    }
}