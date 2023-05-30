package jatx.expense.manager.domain.models

import java.util.*

typealias CellKey = Triple<String, String, Int>
typealias RowKey = Triple<String, String, Int>

data class ParsedXlsx(
    val allCells: Map<CellKey, ExpenseEntry>,
    val allDates: List<Date>,
    val allRowKeys: List<RowKey>
)