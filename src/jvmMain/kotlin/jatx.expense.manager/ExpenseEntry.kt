package jatx.expense.manager

data class ExpenseEntry(
    val cardName: String,
    val category: String,
    val payments: List<Int>
) {
    override fun toString() = "$cardName, $category: $payments"

    companion object {
        fun makeFromStringList(cardName: String, category: String, paymentsStr: List<String>) =
            ExpenseEntry(cardName, category,
                try {
                    paymentsStr.map { it.toInt() }
                } catch (e: NumberFormatException) {
                    println("Wrong number format")
                    listOf()
                }
            )

        fun makeFromDouble(cardName: String, category: String, payment: Double) =
            ExpenseEntry(cardName, category, listOf(payment.toInt()))
    }
}