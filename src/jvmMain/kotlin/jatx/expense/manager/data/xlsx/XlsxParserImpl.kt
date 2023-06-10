package jatx.expense.manager.data.xlsx

import jatx.expense.manager.domain.models.*
import jatx.expense.manager.domain.util.cp1251toUTF8
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.domain.util.plusMonth
import jatx.expense.manager.domain.xlsx.XlsxParser
import jatx.expense.manager.domain.xlsx.XlsxParserFactory
import jatx.expense.manager.res.totalCategory
import org.apache.poi.ss.usermodel.*
import java.io.File
import java.lang.IllegalStateException
import java.util.*

const val theFolderPath = "C:\\Users\\User\\Desktop\\Expense"

class XlsxParserImpl(private val xlsxPath: String): XlsxParser {
    private val allRowKeys = arrayListOf<RowKey>()
    private val allCardNames = arrayListOf<String>()
    private val allCategories = arrayListOf<ArrayList<String>>()

    private val expenseHashMap = hashMapOf<CellKey, ExpenseEntry>()

    override fun parseXlsx(): ExpenseTable {
        val inputStream = File(xlsxPath).inputStream()
        val workbook = WorkbookFactory.create(inputStream)

        val workSheet = workbook.getSheetAt(0)
        val lastRowNum = workSheet.lastRowNum

        val allDates = parseFirstRow(workSheet)

        for (rowNum in 1 until lastRowNum) {
            parseExpenseRow(workSheet, rowNum, allDates)
        }

        inputStream.close()

        return ExpenseTable(expenseHashMap, allDates, allRowKeys)
    }

    private fun parseFirstRow(workSheet: Sheet): List<Date> {
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

    private fun parseExpenseRow(workSheet: Sheet, rowNum: Int, allDates: List<Date>): List<ExpenseEntry>? {
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
        if (category == totalCategory) return null

        val rowKey = RowKey(cardName, category).withIntKey(allCardNames, allCategories)
        allRowKeys.add(rowKey)

        val result = arrayListOf<ExpenseEntry>()

        val dateIterator = allDates.iterator()

        for (cellNum in 3 until lastCellNum) {
            val date = dateIterator.next()
            val cell = row.getCell(cellNum)
            val expenseEntry = when (cell.cellType) {
                CellType.NUMERIC -> {
                    ExpenseEntry.makeFromDouble(rowKey, date, cell.numericCellValue)
                }
                CellType.FORMULA -> {
                    ExpenseEntry.makeFromStringList(rowKey, date, parseFormula(cell.cellFormula))
                }
                else -> null
            }
            expenseEntry?.let {
                expenseHashMap[CellKey(cardName, category, date.monthKey)] = it
            }
        }


        return result
    }
}

class XlsxParserFactoryImpl: XlsxParserFactory {
    override fun newInstance(xlsxPath: String) = XlsxParserImpl(xlsxPath)

}

private fun parseFormula(formula: String): List<String> {
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

fun printParsedXlsx(expenseTable: ExpenseTable) {
    val delim = "\t\t"

    val dateLineBuilder = StringBuilder()
    dateLineBuilder.append("$delim$delim")

    expenseTable.allDates.forEach { date ->
        dateLineBuilder.append(date.formattedMonthAndYear)
        dateLineBuilder.append(delim)
    }

    println(dateLineBuilder.toString())

    expenseTable.allRowKeys.forEach { rowKey ->
        val lineBuilder = StringBuilder()
        lineBuilder.append(rowKey.cardName)
        lineBuilder.append(delim)
        lineBuilder.append(rowKey.category)
        lineBuilder.append(delim)

        expenseTable.allDates.forEach { date ->
            val expenseEntry = expenseTable.getCell(rowKey, date)
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

private fun printCell(cell: Cell) {
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

