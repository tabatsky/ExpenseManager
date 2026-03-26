package jatx.expense.manager.data.firebase

import java.io.File
import java.nio.charset.Charset

data class FirebaseConfig(
    val projectId: String,
    val appId: String,
    val apiKey: String
)

fun readFirebaseConfigFromFile(): FirebaseConfig {
    val lines = File("google-services.txt")
        .readLines(Charset.forName("UTF-8"))
    return FirebaseConfig(
        projectId = lines[0],
        appId = lines[1],
        apiKey = lines[2]
    )
}