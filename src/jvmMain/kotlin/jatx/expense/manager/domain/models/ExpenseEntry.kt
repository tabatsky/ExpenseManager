package jatx.expense.manager.domain.models

import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.res.defaultCommentNegativeAmount
import jatx.expense.manager.res.defaultCommentPositiveAmount
import jatx.expense.manager.res.defaultCommentZeroAmount
import jatx.expense.manager.res.msgWrongNumberFormat
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
        fun makeFromStringList(rowKey: RowKey, date: Date, paymentsStr: List<String>) = ExpenseEntry(
            rowKey.cardName,
            rowKey.category,
            rowKey.rowKeyInt,
            date,
            try {
                paymentsStr.map {
                    PaymentEntry(
                        cardName = rowKey.cardName,
                        category = rowKey.category,
                        rowKeyInt = rowKey.rowKeyInt,
                        date = date,
                        amount = it.toInt(),
                        comment = makeDefaultComment(it.toInt())
                    )
                }
            } catch (e: NumberFormatException) {
                println(msgWrongNumberFormat)
                listOf()
            }
        )

        fun makeFromDouble(rowKey: RowKey, date: Date, amount: Double) = ExpenseEntry(
                rowKey.cardName,
                rowKey.category,
                rowKey.rowKeyInt,
                date,
                if (amount.toInt() != 0)
                    listOf(
                        PaymentEntry(
                            cardName = rowKey.cardName,
                            category = rowKey.category,
                            rowKeyInt = rowKey.rowKeyInt,
                            date = date,
                            amount = amount.toInt(),
                            comment = makeDefaultComment(amount.toInt())
                        )
                    )
                else
                    listOf()
        )
    }
}

fun makeDefaultComment(amount: Int) =
    if (amount > 0)
        defaultCommentPositiveAmount
    else if (amount < 0)
        defaultCommentNegativeAmount
    else
        defaultCommentZeroAmount