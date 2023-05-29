package jatx.expense.manager

import org.apache.poi.ss.usermodel.*
import java.io.File

val xlsPath = "C:\\Users\\User\\Desktop\\Expense\\траты.xlsx"

fun readXlsx() {
    val inputStream = File(xlsPath).inputStream()
    val workbook = WorkbookFactory.create(inputStream)

    val workSheet = workbook.getSheetAt(0)
    val lastRowNum = workSheet.lastRowNum
    println("last row num: $lastRowNum")

    printRow(workSheet, 0)
    parseExpenseRow(workSheet, 10)?.forEach {
        println(it)
    }
    parseExpenseRow(workSheet, 42)?.forEach {
        println(it)
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

fun parseExpenseRow(workSheet: Sheet, rowNum: Int): List<ExpenseEntry>? {
    val row = workSheet.getRow(rowNum)

    val lastCellNum = row.lastCellNum
    if (lastCellNum < 3) return null

    val firstCell = row.getCell(0)
    if (firstCell.cellType != CellType.STRING) return null
    val cardName = firstCell.stringCellValue.cp1251toUTF8()
    if (cardName == "-") return null
    val secondCell = row.getCell(1)
    if (secondCell.cellType != CellType.STRING) return null
    val category = secondCell.stringCellValue.cp1251toUTF8()
    if (category == "Всего") return null

    val result = arrayListOf<ExpenseEntry>()

    for (cellNum in 0 until lastCellNum) {
        val cell = row.getCell(cellNum)
        when (cell.cellType) {
            CellType.NUMERIC -> {
                result.add(
                    ExpenseEntry.makeFromDouble(cardName, category, cell.numericCellValue))
            }
            CellType.FORMULA -> {
                result.add(
                    ExpenseEntry.makeFromStringList(cardName, category, parseFormula(cell.cellFormula)))
            }
            else -> {}
        }
    }

    return result
}

fun printCell(cell: Cell) {
    when (cell.cellType) {
        CellType.STRING -> {
            //println(cell.stringCellValue.cp1251toUTF8())
        }
        CellType.NUMERIC -> {
            if (DateUtil.isCellDateFormatted(cell)) {
                //println(cell.dateCellValue)
            } else {
                //println(cell.numericCellValue)
            }
        }
        CellType.FORMULA -> {
            println(cell.cellFormula)
            val formulaContent = parseFormula(cell.cellFormula)
            println(formulaContent)
        }
        CellType.BLANK -> {
            //println("BLANK")
        }
        else -> TODO("Not implemented yet")
    }
}

fun String.cp1251toUTF8(): String {
    val w1251 = charset("Windows-1251")
    val utf8 = charset("UTF-8")
    return this.toByteArray(utf8).toString(w1251)
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