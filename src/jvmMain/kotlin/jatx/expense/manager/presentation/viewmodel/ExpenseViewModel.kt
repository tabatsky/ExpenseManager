package jatx.expense.manager.presentation.viewmodel

import jatx.expense.manager.data.XlsxParser
import jatx.expense.manager.domain.models.ExpenseEntry
import jatx.expense.manager.domain.models.ParsedXlsx
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExpenseViewModel {
    private val _parsedXlsx: MutableStateFlow<ParsedXlsx?> = MutableStateFlow(null)
    val parsedXlsx = _parsedXlsx.asStateFlow()

    private val _currentExpenseEntry: MutableStateFlow<ExpenseEntry?> = MutableStateFlow(null)
    val currentExpenseEntry = _currentExpenseEntry.asStateFlow()

    fun loadXlsx(xlsPath: String) {
        _parsedXlsx.value = XlsxParser(xlsPath).parseXlsx()
    }

    fun updateCurrentExpenseEntry(expenseEntry: ExpenseEntry) {
        _currentExpenseEntry.value = expenseEntry
    }
}