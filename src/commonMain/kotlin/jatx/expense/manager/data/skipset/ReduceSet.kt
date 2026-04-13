package jatx.expense.manager.data.skipset

const val reduceSetKey = "reduce_set.txt"

object ReduceSet {
    private val theSet: Set<String> by lazy {
        readSet(reduceSetKey)
    }

    fun containsKey(key: String) = theSet.contains(key)
}