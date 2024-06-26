package jatx.expense.manager.domain.repository

import jatx.expense.manager.domain.models.PaymentEntry

interface PaymentRepository {
    suspend fun dropTableIfExists()
    suspend fun createTableIfNotExists()
    suspend fun insertPayments(paymentEntries: List<PaymentEntry>)
    suspend fun insertPayment(paymentEntry: PaymentEntry)
    suspend fun updatePayment(paymentEntry: PaymentEntry)
    suspend fun deletePayment(paymentEntry: PaymentEntry)
    suspend fun selectAll(): List<PaymentEntry>
    suspend fun renameCategory(newCategory: String, cardName: String, category: String)
}