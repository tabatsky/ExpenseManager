package jatx.expense.manager.data.skipset

import android.content.Context
import androidx.core.content.edit
import jatx.expense.manager.di.appComponent

const val prefsName = "prefs"

actual fun readSetLines(setKey: String): List<String> =
    appComponent.androidContextProvider?.getAndroidContext().let { context ->
        val sp = (context as? Context)?.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val text = sp?.getString(setKey, "") ?: ""
        println("read $setKey: $text")
        text.split("\n")
    }

actual fun writeSetLines(setKey: String, lines: List<String>) {
    println("write $setKey: $lines")
    appComponent.androidContextProvider?.getAndroidContext().let { context ->
        val sp = (context as? Context)?.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        sp?.edit(commit = true) {
            putString(setKey, lines.joinToString("\n"))
        }
    }
}