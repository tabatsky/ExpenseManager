package jatx.expense.manager.data.backup

data class BackupData(
    val payments: List<PaymentEntryGson>,
    val lastSyncTime: Long,
    val expenseCommentSet: List<String>? = null,
    val incomingCommentSet: List<String>? = null,
    val incomingSet: List<String>? = null,
    val reduceSet: List<String>? = null,
    val skipCommentSet: List<String>? = null,
    val skipSet: List<String>? = null,
    val totalSkipSet: List<String>? = null
)