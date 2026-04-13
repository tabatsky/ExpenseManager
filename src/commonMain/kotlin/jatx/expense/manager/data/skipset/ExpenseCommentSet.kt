package jatx.expense.manager.data.skipset

const val expenseCommentSetKey = "expense_comment_set.txt"

object ExpenseCommentSet {
    private val theSet: Set<String> by lazy {
        readSet(expenseCommentSetKey)
    }

    fun labelMatching(label: String) = theSet.any { label.startsWith(it) }
}