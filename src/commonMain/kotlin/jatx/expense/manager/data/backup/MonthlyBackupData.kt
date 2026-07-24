package jatx.expense.manager.data.backup

data class MonthlyBackupData(
    val monthKey: Int,
    val payments: List<PaymentEntryGson>
)