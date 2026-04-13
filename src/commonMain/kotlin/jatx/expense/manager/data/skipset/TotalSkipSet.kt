package jatx.expense.manager.data.skipset

const val totalSkipSetKey = "total_skip_set.txt"

object TotalSkipSet {
    private val theSet: Set<String> by lazy {
        readSet(totalSkipSetKey)
    }

    fun containsLabel(label: String) = theSet.contains(label)
}