package jatx.expense.manager.data.xlsx

import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.models.cardNameKey
import jatx.expense.manager.domain.util.cp1251toUTF8
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.domain.xlsx.XlsxSaver
import jatx.expense.manager.domain.xlsx.XlsxSaverFactory
import jatx.expense.manager.res.*
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.Color
import java.io.File

class XlsxSaverImpl(
    private val expenseTable: ExpenseTable,
    private val xlsxPath: String
): XlsxSaver {
    private val allRowNums = arrayListOf<Int>()
    private val allColNums = arrayListOf<Int>()

    init {
        allRowNums.add(1)
        allRowNums.add(2)
        allRowNums.add(3)
        var cardName = totalCardName
        var rowNum = 4
        expenseTable
            .rowKeysWithTotalsNoPlusMinus
            .drop(3)
            .forEach {
                if (it.cardName != cardName) {
                    cardName = it.cardName
                    rowNum += 1
                }
                allRowNums.add(rowNum)
                rowNum += 1
            }

        allColNums.add(2)
        var colNum = 3
        expenseTable
            .datesWithZeroDate
            .drop(1)
            .forEach {
                allColNums.add(colNum)
                colNum += 1
            }
    }
    override fun saveXlsx() {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Sheet1")
        sheet.createFreezePane(3, 2)
        val evaluator = workbook.creationHelper.createFormulaEvaluator()
        val colorMap = workbook.stylesSource.indexedColors

        val firstRow = sheet.createRow(0)
        val firstRowStyle = workbook.createCellStyle()
        val firstRowFont = workbook.createFont()
        firstRowFont.bold = true
        firstRowStyle.setFont(firstRowFont)
        expenseTable
            .datesWithZeroDate
            .forEachIndexed { j, date ->
                val colNum = allColNums[j]
                val cell = firstRow.createCell(colNum)
                cell.setCellValue(date.formattedMonthAndYear.utf8toCP1251())
                cell.cellStyle = firstRowStyle
            }

        expenseTable
            .rowKeysWithTotalsNoPlusMinus
            .forEachIndexed { i, rowKey ->
                val composeColor = if (rowKey.category == lohCategory || rowKey.category == totalLohCategory)
                    redColor
                else
                    when (rowKey.rowKeyInt.cardNameKey) {
                        1 -> blueColor
                        2 -> violetColor
                        3 -> greenColor
                        else -> blackColor
                    }
                val color = XSSFColor(
                    Color(composeColor.red, composeColor.green, composeColor.blue),
                    colorMap
                )
                val rowNum = allRowNums[i]
                val row = sheet.createRow(rowNum)
                val firstCell = row.createCell(0)
                firstCell.setCellValue(rowKey.cardName.utf8toCP1251())
                val firstStyle = workbook.createCellStyle()
                val firstFont = workbook.createFont()
                firstFont.bold = true
                firstFont.setColor(color)
                firstStyle.setFont(firstFont)
                firstCell.cellStyle = firstStyle
                val secondCell = row.createCell(1)
                secondCell.setCellValue(rowKey.category.utf8toCP1251())
                val secondStyle = workbook.createCellStyle()
                val secondFont = workbook.createFont()
                secondFont.bold = true
                secondFont.italic = true
                secondFont.setColor(color)
                secondStyle.setFont(secondFont)
                secondCell.cellStyle = secondStyle
                expenseTable
                    .datesWithZeroDate
                    .forEachIndexed { j, date ->
                        val colNum = allColNums[j]
                        val cell = row.createCell(colNum)
                        val cellStyle = workbook.createCellStyle()
                        val cellFont = workbook.createFont()
                        cellFont.setColor(color)
                        cellStyle.setFont(cellFont)
                        cell.cellStyle = cellStyle

                        val colLetter = getColumnLetter(colNum)
                        val expenseEntry = expenseTable.getCell(rowKey, date)
                        val cellContent = if (i == 0) {
                            val totalCellNames = arrayListOf<String>()
                            expenseTable
                                .rowKeysWithTotalsNoPlusMinus
                                .forEachIndexed { k, rowKey ->
                                    if (rowKey.cardName != totalCardName && rowKey.category == totalCategory) {
                                        val totalRowNum = allRowNums[k]
                                        val totalCellName = "$colLetter${totalRowNum + 1}"
                                        totalCellNames.add(totalCellName)
                                    }
                                }

                            makeSummFormula(totalCellNames)
                        } else if (i == 1) {
                            val totalCellNames = arrayListOf<String>()
                            expenseTable
                                .rowKeysWithTotalsNoPlusMinus
                                .forEachIndexed { k, rowKey ->
                                    if (rowKey.cardName != totalCardName && rowKey.cardName != cashCardName && rowKey.category == totalCategory) {
                                        val totalRowNum = allRowNums[k]
                                        val totalCellName = "$colLetter${totalRowNum + 1}"
                                        totalCellNames.add(totalCellName)
                                    }
                                }

                            makeSummFormula(totalCellNames)
                        } else if (i == 2) {
                            val totalCellNames = arrayListOf<String>()
                            expenseTable
                                .rowKeysWithTotalsNoPlusMinus
                                .forEachIndexed { k, rowKey ->
                                    if (rowKey.category == lohCategory) {
                                        val totalRowNum = allRowNums[k]
                                        val totalCellName = "$colLetter${totalRowNum + 1}"
                                        totalCellNames.add(totalCellName)
                                    }
                                }

                            makeSummFormula(totalCellNames)
                        } else if (j == 0) {
                            val colLetterStart = getColumnLetter(3)
                            val colLetterEnd = getColumnLetter(expenseTable.datesWithZeroDate.size + 1)
                            val cellNameStart = "$colLetterStart${rowNum + 1}"
                            val cellNameEnd = "$colLetterEnd${rowNum + 1}"
                            makeSummFormula(cellNameStart, cellNameEnd)
                        } else if (rowKey.category == totalCategory) {
                            val rowNumStartIndex = expenseTable
                                .rowKeysWithTotalsNoPlusMinus
                                .indexOfFirst { it.cardName == rowKey.cardName } + 1
                            val rowNumStart = allRowNums[rowNumStartIndex]
                            val rowNumEndIndex = expenseTable
                                .rowKeysWithTotalsNoPlusMinus
                                .indexOfLast { it.cardName == rowKey.cardName }
                            val rowNumEnd = allRowNums[rowNumEndIndex]
                            val cellNameStart = "$colLetter${rowNumStart + 1}"
                            val cellNameEnd = "$colLetter${rowNumEnd + 1}"
                            makeSummFormula(cellNameStart, cellNameEnd)
                        } else if (expenseEntry.payments.isNotEmpty()) {
                            joinPaymentsToFormula(expenseEntry.payments)
                        } else {
                            "0"
                        }
                        if (cellContent == "0") {
                            cell.setCellValue(0.0)
                        } else {
                            cell.cellFormula = cellContent
                            evaluator.evaluateFormulaCell(cell)
                        }
                    }
            }

        val outputStream = File(xlsxPath).outputStream()
        workbook.write(outputStream)
        workbook.close()
        outputStream.close()

        println("save xlsx success: $xlsxPath".cp1251toUTF8())
    }
}

class XlsxSaverFactoryImpl: XlsxSaverFactory {
    override fun newInstance(expenseTable: ExpenseTable, xlsxPath: String) =
        XlsxSaverImpl(expenseTable, xlsxPath)

}

private fun joinPaymentsToFormula(payments: List<PaymentEntry>): String {
    val sb = StringBuilder()

    sb.append(payments[0].amount)

    payments
        .drop(1)
        .forEach {
            if (it.amount > 0) {
                sb.append("+")
            }
            sb.append(it.amount)
        }

    return sb.toString()
}

private fun makeSummFormula(cellNameStart: String, cellNameEnd: String) =
    "SUM($cellNameStart:$cellNameEnd)"

private fun makeSummFormula(cellNames: List<String>) = cellNames.joinToString("+")

private fun getColumnLetter(colNum: Int): String {
    val sb = StringBuilder()
    val allLetters = ('a'..'z').toList()
    val letNum0 = colNum / 26 - 1
    if (letNum0 >= 0) sb.append(allLetters[letNum0])
    val letNum1 = colNum % 26
    sb.append(allLetters[letNum1])
    return sb.toString()
}