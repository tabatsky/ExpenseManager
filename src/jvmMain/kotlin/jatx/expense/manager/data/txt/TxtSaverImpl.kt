package jatx.expense.manager.data.txt

import TxtSaver
import jatx.expense.manager.di.AppScope
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.util.formattedForPaymentList
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.res.totalDate
import me.tatarka.inject.annotations.Inject
import java.io.File
import java.io.PrintWriter
import java.util.Date

const val theFolderPath = "C:\\Users\\User\\Desktop\\Expense\\payments"

@AppScope
@Inject
class TxtSaverImpl(): TxtSaver {
    override fun savePayments(payments: List<PaymentEntry>, cardName: String, category: String, date: Date) {
        val dir = File(theFolderPath)
        dir.mkdirs()
        val formattedMonthAndYear = date.formattedMonthAndYear
        val fileName = "${cardName}_${category}_${formattedMonthAndYear}.txt".utf8toCP1251()
        val outFile = File(dir, fileName)
        val pw = PrintWriter(outFile)

        val totalRurAmount = payments.sumOf { it.rurAmount }
        pw.println(totalDate.utf8toCP1251())
        pw.println(totalRurAmount)
        pw.println("-------------------------")
        pw.println("-------------------------")

        payments.reversed().forEach {
            pw.println(it.date.formattedForPaymentList)
            pw.println(it.comment)
            pw.println(it.rurAmount)
            pw.println("-------------------------")
        }

        pw.flush()
        pw.close()
    }
}