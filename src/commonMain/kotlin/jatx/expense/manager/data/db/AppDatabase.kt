package jatx.expense.manager.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import jatx.expense.manager.data.db.dao.PaymentDao
import jatx.expense.manager.data.db.entity.PaymentEntity
import kotlinx.coroutines.Dispatchers
import java.io.File

@Database(entities = [PaymentEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): PaymentDao
}

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(".", "ExpenseManager.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}