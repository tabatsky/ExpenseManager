package jatx.expense.manager.data.skipset

import jatx.expense.manager.domain.util.cp1251toUTF8
import jatx.expense.manager.platform.loadSkipSetsFromFiles

fun readSet(setKey: String) = if (loadSkipSetsFromFiles) {
    val hashSet = hashSetOf<String>()
    try {
        readSetLines(setKey).forEach {
            println(it)
            println(it.cp1251toUTF8())
            hashSet.add(it.cp1251toUTF8())
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    hashSet
} else {
    setOf()
}

expect fun readSetLines(setKey: String): List<String>

