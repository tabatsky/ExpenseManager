package jatx.expense.manager.domain.util

import java.util.*

val Date.monthKey: Int
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        return year * 12 + month
    }

val Date.formattedMonthAndYear: String
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        return "${month + 1}.$year"
    }

val Date.formattedForPaymentList: String
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        return "$dayOfMonth.${month + 1}.$year"
    }

fun Date.plusMonth(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.MONTH, 1)
    return calendar.time
}