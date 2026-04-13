package jatx.expense.manager.data.skipset

const val skipSetKey = "skip_set.txt"

object SkipSet {
    private val theSet: Set<String> by lazy {
        readSet(skipSetKey)
    }

    fun containsLabel(label: String) = theSet.contains(label)
}