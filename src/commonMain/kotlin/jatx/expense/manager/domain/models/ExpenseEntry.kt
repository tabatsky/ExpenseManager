package jatx.expense.manager.domain.models

import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.res.msgWrongNumberFormat
import java.util.Date

data class ExpenseEntry(
    val cardName: String,
    val category: String,
    val rowKeyInt: Int,
    val date: Date,
    private val _payments: List<PaymentEntry>,
    val currencyRates: Map<String, Float> = mapOf(),
    val needSortByDate: Boolean = true
) {
    val label = "$cardName|$category"

    override fun equals(other: Any?) = other is ExpenseEntry &&
            other.cardName == this.cardName &&
            other.category == this.category &&
            other.rowKeyInt == this.rowKeyInt &&
            other.date.monthKey == this.date.monthKey &&
            other._payments.size == this._payments.size &&
            other._payments.sumOf { it.amount } == this._payments.sumOf { it.amount } &&
            other._payments.joinToString("|") { it.comment } ==
            this._payments.joinToString("|") { it.comment } &&
            other._payments.joinToString("|") { it.date.time.toString() } ==
            this._payments.joinToString("|") { it.date.time.toString() }

    override fun hashCode() = cardName.hashCode() +
            10000 * category.hashCode() +
            1000 * rowKeyInt +
            100 * date.monthKey +
            10 * _payments.size +
            _payments.sumOf { it.amount }


    val payments: List<PaymentEntry>
        get() = (if (needSortByDate) {
            _payments.sortedBy {
                val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
                (it.date.time / MILLIS_PER_DAY) + it.id
            }
        } else {
            _payments
        }).map { it.copy(currencyRate = currencyRates[it.currency] ?: 1f) }

    private val monthKey = date.monthKey

    val paymentSum = payments.sumOf { it.rurAmount }
    val currentPaymentSum = payments.sumOf { it.amount }
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
            } catch (_: Exception) {
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
