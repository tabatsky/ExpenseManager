package jatx.expense.manager.data.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.charset.Charset

private val auth by lazy {
    Firebase.auth(Firebase.app("ExpenseManager"))
}

var theUser: FirebaseUser? = null

data class FirebaseAuthData(
    val email: String,
    val password: String
)

fun readFirebaseAuthDataFromFile(): FirebaseAuthData {
    return try {
        val lines = File("firebase-auth.txt")
            .readLines(Charset.forName("UTF-8"))
        FirebaseAuthData(
            email = lines[0],
            password = lines[1]
        )
    } catch (t: Throwable) {
        t.printStackTrace()
        FirebaseAuthData(
            email = "",
            password = ""
        )
    }
}

private suspend fun firebaseSignUp(authData: FirebaseAuthData): AuthResult {
    return auth.createUserWithEmailAndPassword(authData.email, authData.password)
}

private suspend fun firebaseSignIn(authData: FirebaseAuthData): AuthResult  {
    return auth.signInWithEmailAndPassword(authData.email, authData.password)
}

suspend fun firebaseAuth(authData: FirebaseAuthData) = withContext(Dispatchers.IO) {
    try {
        firebaseSignUp(authData)
    } catch (t: Throwable) {
        t.printStackTrace()
    }

    val authResult = try {
        firebaseSignIn(authData)
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }

    println("uid: ${authResult?.user?.uid}")

    theUser = authResult?.user
}
