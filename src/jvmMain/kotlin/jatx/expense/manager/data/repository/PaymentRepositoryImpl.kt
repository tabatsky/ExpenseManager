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
    override suspend fun insertPayments(paymentEntries: List<PaymentEntry>) {
        val paymentEntityQueries = appDatabase.paymentEntityQueries
        paymentEntityQueries.transaction {
            paymentEntries
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

    override suspend fun insertPayment(paymentEntry: PaymentEntry) {
        val paymentEntity = paymentEntry.toDBEntity()
        appDatabase
            .paymentEntityQueries
            .insertPayment(
                cardName = paymentEntity.cardName,
                category = paymentEntity.category,
                rowKeyInt = paymentEntity.rowKeyInt,
                date = paymentEntity.date,
                amount = paymentEntity.amount,
                comment = paymentEntity.comment
            )
    }

    override suspend fun updatePayment(paymentEntry: PaymentEntry) {
        val paymentEntity = paymentEntry.toDBEntity()
        appDatabase
            .paymentEntityQueries
            .updatePayment(
                amount = paymentEntity.amount,
                comment = paymentEntity.comment,
                date = paymentEntity.date,
                id = paymentEntity.id
            )
    }

    override suspend fun deletePayment(paymentEntry: PaymentEntry) {
        val paymentEntity = paymentEntry.toDBEntity()
        appDatabase
            .paymentEntityQueries
            .deletePayment(
                id = paymentEntity.id
            )
    }

    override suspend fun selectAll(): List<PaymentEntry> =
        appDatabase
            .paymentEntityQueries
            .selectAll()
            .executeAsList()
            .map { it.toModelEntry() }

    override suspend fun renameCategory(newCategory: String, cardName: String, category: String) =
        appDatabase
            .paymentEntityQueries
            .renameCategory(newCategory, cardName, category)
}