package jatx.expense.manager.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import jatx.expense.manager.data.db.dao.PaymentDao
import jatx.expense.manager.data.db.entity.PaymentEntity
import jatx.expense.manager.di.AndroidContextProvider

@Database(entities = [PaymentEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): PaymentDao
}

expect fun getDatabaseBuilder(androidContextProvider: AndroidContextProvider? = null): RoomDatabase.Builder<AppDatabase>

expect fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase