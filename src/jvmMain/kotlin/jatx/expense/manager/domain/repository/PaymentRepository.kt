package jatx.expense.manager.domain.repository

import jatx.expense.manager.domain.models.PaymentEntry

interface PaymentRepository {
    suspend fun insertPayments(payments: List<PaymentEntry>)
    suspend fun selectAll(): List<PaymentEntry>
}