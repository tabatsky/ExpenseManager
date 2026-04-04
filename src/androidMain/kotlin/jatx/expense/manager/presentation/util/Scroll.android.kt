package jatx.expense.manager.presentation.util

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
actual fun Modifier.onScroll(coroutineScope: CoroutineScope, perform: suspend (Float, Boolean) -> Unit) = this
    .scrollable(
        orientation = Orientation.Vertical,
        state = rememberScrollableState { delta ->
            println("Scrolled by: $delta")
            coroutineScope.launch {
                perform(-delta, false)
            }
            delta // возвращаем потребленную величину
        }
    )