package jatx.expense.manager.domain.xlsx

import jatx.expense.manager.domain.models.ExpenseTable

interface XlsxSaver {
    fun saveXlsx()
}

interface XlsxSaverFactory {
    fun newInstance(expenseTable: ExpenseTable, xlsxPath: String): XlsxSaver
}