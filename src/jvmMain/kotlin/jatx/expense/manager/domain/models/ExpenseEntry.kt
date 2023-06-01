package jatx.expense.manager.domain.models

import jatx.expense.manager.domain.util.monthKey
import java.util.Date

data class ExpenseEntry(
    val cardName: String,
    val category: String,
    val rowKeyInt: Int,
    val date: Date,
    val payments: List<PaymentEntry>
) {
    private val monthKey = date.monthKey

    val paymentSum = payments.sumOf { it.amount }
    override fun toString() = "$cardName, $category, $monthKey: $payments"

    companion object {
        fun makeFromStringList(cardName: String, category: String, rowKeyInt: Int, date: Date, paymentsStr: List<String>) = ExpenseEntry(
            cardName,
            category,
            rowKeyInt,
            date,
            try {
                paymentsStr.map {
                    PaymentEntry(
                        0,
                        cardName,
                        category,
                        rowKeyInt,
                        date,
                        it.toInt(),
                        makeDefaultComment(it.toInt())
                    )
                }
            } catch (e: NumberFormatException) {
                println("Wrong number format")
                listOf()
            }
        )

        fun makeFromDouble(cardName: String, category: String, rowKeyInt: Int, date: Date, amount: Double) = ExpenseEntry(
                cardName,
                category,
                rowKeyInt,
                date,
                if (amount.toInt() != 0)
                    listOf(
                        PaymentEntry(
                            0,
                            cardName,
                            category,
                            rowKeyInt,
                            date,
                            amount.toInt(),
                            makeDefaultComment(amount.toInt())
                        )
                    )
                else
                    listOf()
        )
    }
}

fun makeDefaultComment(amount: Int) =
    if (amount > 0)
        "Расход"
    else if (amount < 0)
        "Доход"
    else
        "---"