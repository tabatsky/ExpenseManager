package jatx.expense.manager.presentation.viewmodel

import jatx.expense.manager.data.filesystem.XlsxParser
import jatx.expense.manager.domain.models.ExpenseEntry
import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.usecase.LoadExpenseTableFromDBUseCase
import jatx.expense.manager.domain.usecase.SaveExpenseTableToDBUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class ExpenseViewModel(
    private val saveExpenseTableToDBUseCase: SaveExpenseTableToDBUseCase,
    private val loadExpenseTableFromDBUseCase: LoadExpenseTableFromDBUseCase,
    private val coroutineScope: CoroutineScope
) {
    private val _expenseTable: MutableStateFlow<ExpenseTable?> = MutableStateFlow(null)
    val expenseTable = _expenseTable.asStateFlow()

    private val _currentExpenseEntry: MutableStateFlow<ExpenseEntry?> = MutableStateFlow(null)
    val currentExpenseEntry = _currentExpenseEntry.asStateFlow()

    fun loadXlsxToDB(xlsPath: String) {
        val expenseTable = XlsxParser(xlsPath).parseXlsx()
        saveExpenseTableToDBUseCase.execute(expenseTable)
    }

    fun loadExpenseTableFromDB() {
        coroutineScope.launch {
            loadExpenseTableFromDBUseCase.execute().collectLatest {
                _expenseTable.value = it
            }
        }
    }

    fun updateCurrentExpenseEntry(expenseEntry: ExpenseEntry) {
        _currentExpenseEntry.value = expenseEntry
    }
}