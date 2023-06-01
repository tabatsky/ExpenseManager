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

val Int.dateFromMonthKey: Date
    get() {
        val month = this % 12
        val year = this / 12

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)

        return calendar.time
    }

val Date.asDbPresentation: Long
    get() {
        var result = 0L

        val calendar = Calendar.getInstance()
        calendar.time = this

        val year = calendar.get(Calendar.YEAR)
        result += year
        result *= 100

        val month = calendar.get(Calendar.MONTH)
        result += month
        result *= 100

        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        result += dayOfMonth
        result *= 100

        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        result += hourOfDay
        result *= 100

        val minute = calendar.get(Calendar.MINUTE)
        result += minute
        result *= 100

        val second = calendar.get(Calendar.SECOND)
        result += second
        result *= 1000

        val millisecond = calendar.get(Calendar.MILLISECOND)
        result += millisecond

        return result
}

val Long.fromDbPresentation: Date
    get() {
        var tmp = this
        val millisecond = tmp % 1000
        tmp /= 1000
        val second = tmp % 100
        tmp /= 100
        val minute = tmp % 100
        tmp /= 100
        val hourOfDay = tmp % 100
        tmp /= 100
        val dayOfMonth = tmp % 100
        tmp /= 100
        val month = tmp % 100
        tmp /= 100
        val year = tmp

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year.toInt())
        calendar.set(Calendar.MONTH, month.toInt())
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth.toInt())
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay.toInt())
        calendar.set(Calendar.MINUTE, minute.toInt())
        calendar.set(Calendar.SECOND, second.toInt())
        calendar.set(Calendar.MILLISECOND, millisecond.toInt())

        return calendar.time
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