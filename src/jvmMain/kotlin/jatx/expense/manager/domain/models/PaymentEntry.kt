package jatx.expense.manager.domain.models

import java.util.*

data class PaymentEntry(
    val id: Int,
    val dbKey: Int,
    val date: Date,
    val amount: Int,
    val comment: String
)
