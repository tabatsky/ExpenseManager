package jatx.expense.manager.domain.xlsx

import jatx.expense.manager.domain.models.ExpenseTable

interface XlsxParser {
    fun parseXlsx(): ExpenseTable
}

interface XlsxParserFactory {
    fun newInstance(xlsxPath: String): XlsxParser
}