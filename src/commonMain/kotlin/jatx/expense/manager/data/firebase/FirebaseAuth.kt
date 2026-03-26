package jatx.expense.manager.data.firebase

import java.io.File
import java.nio.charset.Charset

data class FirebaseAuthData(
    val email: String,
    val password: String
)

fun readFirebaseAuthDataFromFile(): FirebaseAuthData {
    val lines = File("firebase-auth.txt")
        .readLines(Charset.forName("UTF-8"))
    return FirebaseAuthData(
        email = lines[0],
        password = lines[1]
    )
}

