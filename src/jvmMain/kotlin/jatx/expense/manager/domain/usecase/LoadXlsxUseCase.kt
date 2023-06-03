package jatx.expense.manager.domain.usecase

import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.xlsx.XlsxParserFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LoadXlsxUseCase(
    private val xlsxParserFactory: XlsxParserFactory
) {
    fun execute(xlsxPath: String): Flow<ExpenseTable> = flow {
        val xlsxParser = xlsxParserFactory.newInstance(xlsxPath)
        val expenseTable = xlsxParser.parseXlsx()
        emit(expenseTable)
    }.flowOn(Dispatchers.IO)
}