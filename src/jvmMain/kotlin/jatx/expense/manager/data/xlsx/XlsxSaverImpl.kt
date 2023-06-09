package jatx.expense.manager.data.xlsx

import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.util.cp1251toUTF8
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.domain.xlsx.XlsxSaver
import jatx.expense.manager.domain.xlsx.XlsxSaverFactory
import jatx.expense.manager.res.totalCardName
import jatx.expense.manager.res.totalCategory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

const val outXlsxPath = "C:\\Users\\User\\Desktop\\Expense\\�������.xlsx"

class XlsxSaverImpl(
    private val expenseTable: ExpenseTable,
    private val xlsxPath: String
): XlsxSaver {
    private val allRowNums = arrayListOf<Int>()
    private val allColNums = arrayListOf<Int>()

    init {
        allRowNums.add(1)
        var cardName = totalCardName
        var rowNum = 3
        expenseTable
            .allRowKeys
            .drop(1)
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
            .allDates
            .drop(1)
            .forEach {
                allColNums.add(colNum)
                colNum += 1
            }
    }
    override fun saveXlsx() {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Sheet1")
        val evaluator = workbook.creationHelper.createFormulaEvaluator()

        val firstRow = sheet.createRow(0)
        expenseTable
            .allDates
            .forEachIndexed { j, date ->
                val colNum = allColNums[j]
                val cell = firstRow.createCell(colNum)
                cell.setCellValue(date.formattedMonthAndYear.utf8toCP1251())
            }

        expenseTable
            .allRowKeys
            .forEachIndexed { i, rowKey ->
                val rowNum = allRowNums[i]
                val row = sheet.createRow(rowNum)
                val firstCell = row.createCell(0)
                firstCell.setCellValue(rowKey.cardName.utf8toCP1251())
                val secondCell = row.createCell(1)
                secondCell.setCellValue(rowKey.category.utf8toCP1251())
                expenseTable
                    .allDates
                    .forEachIndexed { j, date ->
                        val colNum = allColNums[j]
                        val cell = row.createCell(colNum)

                        val colLetter = getColumnLetter(colNum)
                        val expenseEntry = expenseTable.getCell(rowKey, date)
                        val cellContent = if (i == 0) {
                            val totalCellNames = arrayListOf<String>()
                            expenseTable
                                .allRowKeys
                                .forEachIndexed { k, rowKey ->
                                    if (rowKey.cardName != totalCardName && rowKey.category == totalCategory) {
                                        val totalRowNum = allRowNums[k]
                                        val totalCellName = "$colLetter${totalRowNum + 1}"
                                        totalCellNames.add(totalCellName)
                                    }
                                }

                            makeSummFormula(totalCellNames)
                        } else if (j == 0) {
                            val colLetterStart = getColumnLetter(3)
                            val colLetterEnd = getColumnLetter(expenseTable.allDates.size + 1)
                            val cellNameStart = "$colLetterStart${rowNum + 1}"
                            val cellNameEnd = "$colLetterEnd${rowNum + 1}"
                            makeSummFormula(cellNameStart, cellNameEnd)
                        } else if (rowKey.category == totalCategory) {
                            val rowNumStartIndex = expenseTable
                                .allRowKeys
                                .indexOfFirst { it.cardName == rowKey.cardName } + 1
                            val rowNumStart = allRowNums[rowNumStartIndex]
                            val rowNumEndIndex = expenseTable
                                .allRowKeys
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