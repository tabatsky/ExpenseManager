package jatx.expense.manager.data.backup

import jatx.expense.manager.di.AndroidContextProvider
import jatx.expense.manager.di.AppScope
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class BackupTimeKeeper(private val androidContextProvider: AndroidContextProvider?) {
    var lastSyncTime: Long
        get() = readLastSyncTime(androidContextProvider)
        set(value) {
            writeLastSyncTime(value, androidContextProvider)
        }
}

expect fun readLastSyncTime(androidContextProvider: AndroidContextProvider?): Long

expect fun writeLastSyncTime(time: Long, androidContextProvider: AndroidContextProvider?)