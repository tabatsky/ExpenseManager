package jatx.expense.manager.data.repository

import jatx.expense.manager.data.converters.toDBEntity
import jatx.expense.manager.data.converters.toModelEntry
import jatx.expense.manager.db.AppDatabase
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.repository.PaymentRepository

class PaymentRepositoryImpl(
    private val appDatabase: AppDatabase
): PaymentRepository {

    override suspend fun dropTableIfExists() {
        appDatabase
            .paymentEntityQueries
            .dropTableIfExists()
    }
    override suspend fun createTableIfNotExists() {
        appDatabase
            .paymentEntityQueries
            .createTableIfNotExists()
    }
    override suspend fun insertPayments(payments: List<PaymentEntry>) {
        val paymentEntityQueries = appDatabase.paymentEntityQueries
        paymentEntityQueries.transaction {
            payments
                .map { it.toDBEntity() }
                .forEach {
                    paymentEntityQueries.insertPayment(
                        cardName = it.cardName,
                        category = it.category,
                        rowKeyInt = it.rowKeyInt,
                        date = it.date,
                        amount = it.amount,
                        comment = it.comment
                    )
                }
        }
    }

    override suspend fun selectAll(): List<PaymentEntry> =
        appDatabase
            .paymentEntityQueries
            .selectAll()
            .executeAsList()
            .map { it.toModelEntry() }
}