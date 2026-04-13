package jatx.expense.manager.data.skipset

const val skipCommentSetKey = "skip_comment_set.txt"

object SkipCommentSet {
    private val theSet: Set<String> by lazy {
        readSet(skipCommentSetKey)
    }

    fun containsLabel(label: String) = theSet.contains(label)
}