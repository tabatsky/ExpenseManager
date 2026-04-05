package jatx.expense.manager.data.skipset

import jatx.expense.manager.domain.util.cp1251toUTF8
import jatx.expense.manager.platform.loadSkipSetsFromFiles
import java.io.File
import java.nio.charset.Charset

object TotalSkipSet {
    private val theSet: Set<String> by lazy {
        if (loadSkipSetsFromFiles) {
            val hashSet = hashSetOf<String>()
            try {
                File("total_skip_set.txt").readLines(Charset.forName("UTF-8")).forEach {
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
    }

    fun containsLabel(label: String) = theSet.contains(label)
}