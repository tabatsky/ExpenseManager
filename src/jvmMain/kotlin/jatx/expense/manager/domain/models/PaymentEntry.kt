package jatx.expense.manager.domain.models

import java.util.*

data class PaymentEntry(
    val id: Long,
    val cardName: String,
    val category: String,
    val rowKeyInt: Int,
    val date: Date,
    val amount: Int,
    val comment: String
)
