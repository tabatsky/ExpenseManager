package jatx.expense.manager.data.firebase

import android.app.Application
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import jatx.expense.manager.di.AndroidContextProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun initFirebase(config: FirebaseConfig, androidContextProvider: AndroidContextProvider?) = withContext(Dispatchers.IO) {
    FirebasePlatform.initializeFirebasePlatform(
        object : FirebasePlatform() {
            val storage = mutableMapOf<String, String>()
            override fun store(key: String, value: String) = storage.set(key, value)
            override fun retrieve(key: String) = storage[key]
            override fun clear(key: String) {
                storage.remove(key)
            }

            override fun log(msg: String) = println(msg)
        }
    )
    val options = FirebaseOptions(
        applicationId = config.appId,
        apiKey = config.apiKey,
        projectId = config.projectId
    )
    Firebase.initialize(
        context = Application(),
        options = options,
        name = "ExpenseManager"
    )
}