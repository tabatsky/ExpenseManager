package jatx.expense.manager.data.firebase

import dev.gitlive.firebase.FirebaseApp
import jatx.expense.manager.di.AndroidContextProvider

expect suspend fun initFirebase(config: FirebaseConfig, androidContextProvider: AndroidContextProvider? = null): FirebaseApp