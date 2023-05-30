package jatx.expense.manager.data

import jatx.expense.manager.domain.models.ExpenseEntry
import jatx.expense.manager.domain.models.ParsedXlsx
import jatx.expense.manager.domain.models.RowKey
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.domain.util.plusMonth
import org.apache.poi.ss.usermodel.*
import java.io.File
import java.lang.IllegalStateException
import java.util.*

val theXlsPath = "C:\\Users\\User\\Desktop\\Expense\\траты.xlsx"

val skipLoh = false

fun parseXlsx(xlsPath: String): ParsedXlsx {
    val inputStream = File(xlsPath).inputStream()
    val workbook = WorkbookFactory.create(inputStream)

    val workSheet = workbook.getSheetAt(0)
    val lastRowNum = workSheet.lastRowNum

    val allDates = parseFirstRow(workSheet)

    val allRowKeys = arrayListOf<RowKey>()
    val allCardNames = arrayListOf<String>()
    val allCategories = arrayListOf<ArrayList<String>>()

    val expenseHashMap = hashMapOf<Triple<String, String, Int>, ExpenseEntry>()

    for (rowNum in 1 until lastRowNum) {
        val expenseEntry = parseExpenseRow(workSheet, rowNum, allDates)
        expenseEntry?.getOrNull(0)?.let {
            val key1 = allCardNames
                .indexOf(it.cardName)
                .takeIf { it >= 0 }
                ?: run {
                    allCardNames.add(it.cardName)
                    allCategories.add(arrayListOf())
                    allCardNames.size - 1
                }
            val categories = allCategories[key1]
            val key2 = categories
                .indexOf(it.category)
                .takeIf { it >= 0 }
                ?: run {
                    categories.add(it.category)
                    categories.size - 1
                }
            val key = 1000 * (key1 + 1) + key2 + 1

            val rowKey = Triple(it.cardName, it.category, key)
            allRowKeys.add(rowKey)
        }
        expenseEntry?.forEach {
            expenseHashMap[Triple(it.cardName, it.category, it.monthKey)] = it
        }
    }

    return ParsedXlsx(expenseHashMap, allDates, allRowKeys)
}

fun parseFirstRow(workSheet: Sheet): List<Date> {
    val firstRow = workSheet.getRow(0)

    val lastCellNum = firstRow.lastCellNum
    if (lastCellNum < 4) throw IllegalStateException("First row is not quite long")

    val result = arrayListOf<Date>()

    for (cellNum in 3 until lastCellNum) {
        val cell = firstRow.getCell(cellNum)
        if (cell.cellType != CellType.NUMERIC)
            throw IllegalStateException("Date cell is not numeric")
        if (!DateUtil.isCellDateFormatted(cell))
            throw IllegalStateException("Date cell is not containing date")
        val date = cell.dateCellValue
        result.add(date)
    }

    val currentMonthKey = Date().monthKey

    var lastDate = result.last()

    while (lastDate.monthKey < currentMonthKey) {
        lastDate = lastDate.plusMonth()
        result.add(lastDate)
    }

    return result
}

fun parseExpenseRow(workSheet: Sheet, rowNum: Int, allDates: List<Date>): List<ExpenseEntry>? {
    val row = workSheet.getRow(rowNum)

    val lastCellNum = row.lastCellNum
    if (lastCellNum < 4) return null

    val firstCell = row.getCell(0)
    if (firstCell.cellType != CellType.STRING) return null
    val cardName = firstCell.stringCellValue.cp1251toUTF8()
    if (cardName == "-") return null
    val secondCell = row.getCell(1)
    if (secondCell.cellType != CellType.STRING) return null
    val category = secondCell.stringCellValue.cp1251toUTF8()
    if (category == "Всего".cp1251toUTF8()) return null
    if (skipLoh && category == "Лоханулся".cp1251toUTF8()) return null

    val result = arrayListOf<ExpenseEntry>()

    val dateIterator = allDates.iterator()

    for (cellNum in 3 until lastCellNum) {
        val date = dateIterator.next()
        val cell = row.getCell(cellNum)
        when (cell.cellType) {
            CellType.NUMERIC -> {
                result.add(
                    ExpenseEntry.makeFromDouble(cardName, category, date, cell.numericCellValue))
            }
            CellType.FORMULA -> {
                result.add(
                    ExpenseEntry.makeFromStringList(cardName, category, date, parseFormula(cell.cellFormula)))
            }
            else -> {}
        }
    }

    return result
}

fun parseFormula(formula: String): List<String> {
    val result = arrayListOf<String>()
    var sb = StringBuilder()

    fun newSb() {
        if (sb.isNotEmpty()) {
            result.add(sb.toString())
        }
        sb = StringBuilder()
    }

    formula.toCharArray().forEach {
        when (it) {
            '+' -> {
                newSb()
            }
            '-' -> {
                newSb()
                sb.append('-')
            }
            else -> {
                sb.append(it)
            }
        }
    }
    newSb()

    return result
}


fun printParsedXlsx(parsedXlsx: ParsedXlsx) {
    val delim = "\t\t"

    val dateLineBuilder = StringBuilder()
    dateLineBuilder.append("$delim$delim")

    parsedXlsx.allDates.forEach { date ->
        dateLineBuilder.append(date.formattedMonthAndYear)
        dateLineBuilder.append(delim)
    }

    println(dateLineBuilder.toString())

    parsedXlsx.allRowKeys.forEach { rowKey ->
        val lineBuilder = StringBuilder()
        lineBuilder.append(rowKey.first)
        lineBuilder.append(delim)
        lineBuilder.append(rowKey.second)
        lineBuilder.append(delim)

        parsedXlsx.allDates.forEach { date ->
            val expenseEntry = parsedXlsx.allCells[Triple(rowKey.first, rowKey.second, date.monthKey)]
                ?: ExpenseEntry.makeFromDouble(rowKey.first, rowKey.second, date, 0.0)
            lineBuilder.append(expenseEntry.paymentSum)
            lineBuilder.append(delim)
        }

        println(lineBuilder.toString())
    }
}

fun printRow(workSheet: Sheet, rowNum: Int) {
    val row = workSheet.getRow(rowNum)
    val lastCellNum = row.lastCellNum
    for (cellNum in 0 until lastCellNum) {
        val cell = row.getCell(cellNum)
        printCell(cell)
    }
}

fun printCell(cell: Cell) {
    when (cell.cellType) {
        CellType.STRING -> {
            println(cell.stringCellValue.cp1251toUTF8())
        }
        CellType.NUMERIC -> {
            if (DateUtil.isCellDateFormatted(cell)) {
                println(cell.dateCellValue)
            } else {
                println(cell.numericCellValue)
            }
        }
        CellType.FORMULA -> {
            println(cell.cellFormula)
            val formulaContent = parseFormula(cell.cellFormula)
            println(formulaContent)
        }
        CellType.BLANK -> {
            println("BLANK")
        }
        else -> TODO("Not implemented yet")
    }
}

fun String.cp1251toUTF8(): String {
    val w1251 = charset("Windows-1251")
    val utf8 = charset("UTF-8")
    return this.toByteArray(utf8).toString(w1251)
}

fun String.utf8toCP1251(): String {
    val w1251 = charset("Windows-1251")
    val utf8 = charset("UTF-8")
    return this.toByteArray(w1251).toString(utf8)
}