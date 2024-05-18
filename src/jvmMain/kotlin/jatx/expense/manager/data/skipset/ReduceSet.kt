package jatx.expense.manager.data.skipset

import jatx.expense.manager.domain.util.cp1251toUTF8
import java.io.File
import java.nio.charset.Charset

object ReduceSet {
    private val theSet: Set<String> by lazy {
        val hashSet = hashSetOf<String>()
        try {
            File("reduce_set.txt").readLines(Charset.forName("UTF-8")).forEach {
                println(it)
                println(it.cp1251toUTF8())
                hashSet.add(it.cp1251toUTF8())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        hashSet
    }

    fun containsKey(key: String) = theSet.contains(key)
}