package jatx.expense.manager.presentation.viewmodel

import jatx.expense.manager.data.parseXlsx
import jatx.expense.manager.domain.models.ParsedXlsx
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExpenseViewModel {
    private val _parsedXlsx: MutableStateFlow<ParsedXlsx?> = MutableStateFlow(null)
    val parsedXlsx = _parsedXlsx.asStateFlow()
    fun loadXlsx(xlsPath: String) {
        _parsedXlsx.value = parseXlsx(xlsPath)
    }
}