package jatx.expense.manager.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import jatx.expense.manager.di.AndroidContextProvider
import kotlinx.coroutines.Dispatchers
import java.io.File

actual fun getDatabaseBuilder(androidContextProvider: AndroidContextProvider?): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(".", "ExpenseManager.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}

actual fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}