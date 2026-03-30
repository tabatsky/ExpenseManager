package jatx.expense.manager.data.backup

import jatx.expense.manager.domain.models.PaymentEntry
import java.util.Date

data class PaymentEntryGson(
    val id: Long,
    val cardName: String,
    val category: String,
    val rowKeyInt: Int,
    val date: Long,
    val amount: Int,
    val comment: String,
    val currencyRate: Float = 1f
)

fun PaymentEntry.toPaymentEntryGson() = PaymentEntryGson(
    id = id,
    cardName = cardName,
    category = category,
    rowKeyInt = rowKeyInt,
    date = date.time,
    amount = amount,
    comment = comment,
    currencyRate = currencyRate
)

fun PaymentEntryGson.toPaymentEntry() = PaymentEntry(
    id = id,
    cardName = cardName,
    category = category,
    rowKeyInt = rowKeyInt,
    date = Date(date),
    amount = amount,
    comment = comment,
    currencyRate = currencyRate
)