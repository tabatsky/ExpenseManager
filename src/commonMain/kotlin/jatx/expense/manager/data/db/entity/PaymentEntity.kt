package jatx.expense.manager.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paymentEntity")
class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    val cardName: String,
    val category: String,
    val rowKeyInt: Int,
    val date: Long,
    val amount: Int,
    val comment: String,
    @ColumnInfo(defaultValue="RUR") val currency: String
)