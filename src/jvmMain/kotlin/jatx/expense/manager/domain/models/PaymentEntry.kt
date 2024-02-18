package jatx.expense.manager.domain.models

import jatx.expense.manager.res.defaultCommentNegativeAmount
import jatx.expense.manager.res.defaultCommentPositiveAmount
import jatx.expense.manager.res.defaultCommentZeroAmount
import java.util.*

data class PaymentEntry(
    val id: Long = 0,
    val cardName: String,
    val category: String,
    val rowKeyInt: Int,
    val date: Date,
    val amount: Int,
    val comment: String,
    val currency: String = ""
)

fun makeDefaultComment(amount: Int) =
    if (amount > 0)
        defaultCommentPositiveAmount
    else if (amount < 0)
        defaultCommentNegativeAmount
    else
        defaultCommentZeroAmount