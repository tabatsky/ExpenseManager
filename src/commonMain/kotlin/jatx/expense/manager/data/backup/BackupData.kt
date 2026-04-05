package jatx.expense.manager.data.backup

data class BackupData(
    val payments: List<PaymentEntryGson>,
    val lastSyncTime: Long
)