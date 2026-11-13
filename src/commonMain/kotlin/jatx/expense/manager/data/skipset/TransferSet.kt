package jatx.expense.manager.data.skipset

const val transferSetKey = "transfer_set.txt"

object TransferSet {
    private val theSet: Set<String> by lazy {
        readSet(transferSetKey)
    }

    fun containsLabel(label: String) = theSet.contains(label)
}