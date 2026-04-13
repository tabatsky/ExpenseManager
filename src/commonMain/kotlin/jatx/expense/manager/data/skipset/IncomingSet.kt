package jatx.expense.manager.data.skipset

const val incomingSetKey = "incoming_set.txt"

object IncomingSet {
    private val theSet: Set<String> by lazy {
        readSet(incomingSetKey)
    }

    fun containsLabel(label: String) = theSet.contains(label)
}