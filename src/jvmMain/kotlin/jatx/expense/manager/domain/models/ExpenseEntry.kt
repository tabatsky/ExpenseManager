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
    val monthKey = date.monthKey
    val dbKey = makeDbKey(rowKeyInt, date)

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
                        makeDbKey(rowKeyInt, date),
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
                            makeDbKey(rowKeyInt, date),
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

fun makeDbKey(rowKeyInt: Int, date: Date): Int {
    val monthKey = date.monthKey
    return rowKeyInt * 100000 + monthKey
}

fun makeDefaultComment(amount: Int) =
    if (amount > 0)
        "������"
    else if (amount < 0)
        "�����"
    else
        "---"