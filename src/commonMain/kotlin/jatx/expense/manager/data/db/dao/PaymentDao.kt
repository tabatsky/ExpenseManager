package jatx.expense.manager.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import jatx.expense.manager.data.db.entity.PaymentEntity

@Dao
interface PaymentDao {
    @Query("SELECT * FROM paymentEntity")
    suspend fun selectAll(): List<PaymentEntity>

    @Insert
    suspend fun insertPayments(paymentEntities: List<PaymentEntity>)

    @Insert
    suspend fun insertPayment(paymentEntity: PaymentEntity)

    @Query("UPDATE paymentEntity SET amount=:amount, comment=:comment, date=:date WHERE id=:id")
    suspend fun updatePayment(amount: Int, comment: String, date: Long, id: Long)

    @Query("DELETE FROM paymentEntity WHERE id=:id")
    suspend fun deletePayment(id: Long)

    @Query("UPDATE paymentEntity SET category=:newCategory WHERE cardName=:cardName AND category=:oldCategory")
    suspend fun renameCategory(newCategory: String, cardName: String, oldCategory: String)
}