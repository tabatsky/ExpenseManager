package jatx.expense.manager.data.firebase

import android.content.Context
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import jatx.expense.manager.di.AndroidContextProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun initFirebase(config: FirebaseConfig, androidContextProvider: AndroidContextProvider?) = withContext(Dispatchers.IO) {
    val context = androidContextProvider?.getAndroidContext() as Context
    val options = FirebaseOptions(
        applicationId = config.appId,
        apiKey = config.apiKey,
        projectId = config.projectId
    )
    Firebase.initialize(
        context = context,
        options = options,
        name = "ExpenseManager"
    )
}