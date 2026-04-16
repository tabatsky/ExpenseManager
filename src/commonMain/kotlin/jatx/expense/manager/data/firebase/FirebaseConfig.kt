package jatx.expense.manager.data.firebase

data class FirebaseConfig(
    val projectId: String,
    val appId: String,
    val apiKey: String
)

fun defaultFirebaseConfig() = FirebaseConfig(
    projectId = projectId,
    appId = appId,
    apiKey = apiKey
)
