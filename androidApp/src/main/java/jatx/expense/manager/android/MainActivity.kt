package jatx.expense.manager.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.lifecycleScope
import jatx.expense.manager.data.firebase.FirebaseAuthData
import jatx.expense.manager.data.firebase.defaultFirebaseConfig
import jatx.expense.manager.data.firebase.email
import jatx.expense.manager.data.firebase.password
import jatx.expense.manager.di.AndroidContextProvider
import jatx.expense.manager.di.AppComponent
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.di.create
import jatx.expense.manager.presentation.view.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val coroutineScope = lifecycleScope
        val androidContextProvider = object: AndroidContextProvider {
            override fun getAndroidContext() = applicationContext
        }

        appComponent = AppComponent::class.create(coroutineScope, androidContextProvider)

        appComponent.menuCallbacks.onAppExit = ::exitApp

        val firebaseConfig = defaultFirebaseConfig()
        val firebaseAuthData = FirebaseAuthData(
            email = email,
            password = password
        )
        appComponent.expenseViewModel.onAppStart(firebaseConfig, firebaseAuthData)

        enableEdgeToEdge()
        setContent {
            BackHandler(enabled = true) {
                exitApp()
            }
            MaterialTheme {
                MainScreen()
            }
        }
    }

    private fun exitApp() {
        appComponent.expenseViewModel.onAppExit {
            Log.e("MainActivity", "finish")
            finish()
        }
    }
}
