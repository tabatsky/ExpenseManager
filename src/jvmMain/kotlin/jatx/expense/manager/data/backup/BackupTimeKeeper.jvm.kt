package jatx.expense.manager.data.backup

import jatx.expense.manager.di.AndroidContextProvider
import java.io.File
import java.nio.charset.Charset

actual fun readLastSyncTime(androidContextProvider: AndroidContextProvider?): Long {
    return try {
        File("last_sync_time.txt")
            .readText(Charset.forName("UTF-8"))
            .trim()
            .toLong()
    } catch (t: Throwable) {
        t.printStackTrace()
        0L
    }
}

actual fun writeLastSyncTime(time: Long, androidContextProvider: AndroidContextProvider?) {
    File("last_sync_time.txt")
        .writeText("$time\n")
}