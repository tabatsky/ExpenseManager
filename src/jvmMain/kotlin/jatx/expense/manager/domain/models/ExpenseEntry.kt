package jatx.expense.manager.domain.models

import jatx.expense.manager.domain.util.monthKey
import java.util.Date

data class ExpenseEntry(
    val cardName: String,
    val category: String,
    val date: Date,
    val payments: List<Int>
) {
    val monthKey = date.monthKey

    val paymentSum = payments.sum()
    override fun toString() = "$cardName, $category, $monthKey: $payments"

    companion object {
        fun makeFromStringList(cardName: String, category: String, date: Date, paymentsStr: List<String>) =
            ExpenseEntry(cardName, category, date,
                try {
                    paymentsStr.map { it.toInt() }
                } catch (e: NumberFormatException) {
                    println("Wrong number format")
                    listOf()
                }
            )

        fun makeFromDouble(cardName: String, category: String, date: Date, payment: Double) =
            ExpenseEntry(cardName, category, date, listOf(payment.toInt()))
    }
}