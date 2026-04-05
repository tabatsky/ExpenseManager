package jatx.expense.manager.data.backup

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import jatx.expense.manager.di.AndroidContextProvider

const val spName = "ExpenseManagerPrefs"
const val lastSyncTimeKey = "LAST_SYNC_TIME"

actual fun readLastSyncTime(androidContextProvider: AndroidContextProvider?): Long {
    return (androidContextProvider?.getAndroidContext() as? Context)
        ?.getSharedPreferences(spName, MODE_PRIVATE)
        ?.getLong(lastSyncTimeKey, 0L)
        ?: 0L
}

actual fun writeLastSyncTime(time: Long, androidContextProvider: AndroidContextProvider?) {
    (androidContextProvider?.getAndroidContext() as? Context)
        ?.getSharedPreferences(spName, MODE_PRIVATE)
        ?.edit {
            putLong(lastSyncTimeKey, time)
            commit()
        }
}