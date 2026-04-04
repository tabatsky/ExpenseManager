package jatx.expense.manager.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import jatx.expense.manager.di.AndroidContextProvider
import kotlinx.coroutines.Dispatchers

actual fun getDatabaseBuilder(androidContextProvider: AndroidContextProvider?): RoomDatabase.Builder<AppDatabase> {
    val dbName = "ExpenseManager.db"
    val context = androidContextProvider?.getAndroidContext() as Context
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = dbName
    )
}

actual fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}