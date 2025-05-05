package jatx.expense.manager.data.repository

import androidx.room.execSQL
import androidx.room.useWriterConnection
import jatx.expense.manager.data.converters.toDBEntity
import jatx.expense.manager.data.converters.toModelEntry
import jatx.expense.manager.data.db.AppDatabase
import jatx.expense.manager.di.AppScope
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.repository.PaymentRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class PaymentRepositoryImpl(
    private val appDatabase: AppDatabase
): PaymentRepository {
    override suspend fun dropTableIfExists() {
        appDatabase
            .useWriterConnection {
                it.execSQL("DROP TABLE IF EXISTS paymentEntity")
            }
    }

    override suspend fun createTableIfNotExists() {
        appDatabase
            .useWriterConnection {
                it.execSQL("""
                    CREATE TABLE IF NOT EXISTS paymentEntity
                    (id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cardName TEXT NOT NULL,
                    category TEXT NOT NULL,
                    rowKeyInt INTEGER NOT NULL,
                    date INTEGER NOT NULL,
                    amount INTEGER NOT NULL,
                    comment TEXT NOT NULL,
                    currency TEXT NOT NULL DEFAULT 'RUR'
                    )
                """.trimIndent())
            }
    }

    override suspend fun insertPayments(paymentEntries: List<PaymentEntry>) =
        appDatabase.getDao().insertPayments(paymentEntries.map { it.toDBEntity() })

    override suspend fun insertPayment(paymentEntry: PaymentEntry) =
        appDatabase.getDao().insertPayment(paymentEntry.toDBEntity())

    override suspend fun updatePayment(paymentEntry: PaymentEntry) {
        val paymentEntity = paymentEntry.toDBEntity()
        appDatabase
            .getDao()
            .updatePayment(
                amount = paymentEntity.amount,
                comment = paymentEntity.comment,
                date = paymentEntity.date,
                id = paymentEntity.id ?: 0L
            )
    }

    override suspend fun deletePayment(paymentEntry: PaymentEntry) {
        val paymentEntity = paymentEntry.toDBEntity()
        appDatabase
            .getDao()
            .deletePayment(
                id = paymentEntity.id ?: 0L
            )
    }

    override suspend fun selectAll(): List<PaymentEntry> =
        appDatabase
            .getDao()
            .selectAll()
            .map { it.toModelEntry() }

    override suspend fun renameCategory(newCategory: String, cardName: String, category: String) =
        appDatabase
            .getDao()
            .renameCategory(newCategory, cardName, category)
}