package jatx.expense.manager.data.skipset

import jatx.expense.manager.domain.util.cp1251toUTF8
import jatx.expense.manager.platform.loadSkipSets

fun readSet(setKey: String) = if (loadSkipSets) {
    val hashSet = hashSetOf<String>()
    try {
        readSetLines(setKey).forEach {
            println(it)
            println(it.cp1251toUTF8())
            if (it.cp1251toUTF8().isNotEmpty()) {
                hashSet.add(it.cp1251toUTF8())
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    hashSet
} else {
    setOf()
}

expect fun readSetLines(setKey: String): List<String>

expect fun writeSetLines(setKey: String, lines: List<String>)
