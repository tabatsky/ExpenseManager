package jatx.expense.manager.data.skipset

const val incomingCommentSetKey = "incoming_comment_set.txt"

object IncomingCommentSet {
    private val theSet: Set<String> by lazy {
        readSet(incomingCommentSetKey)
    }

    fun labelMatching(label: String) = theSet.any { label.startsWith(it) }
}