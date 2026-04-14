package jatx.expense.manager.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

var lastEventTime = 0L

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun Modifier.onScroll(coroutineScope: CoroutineScope, perform: suspend (Float, Boolean) -> Unit) =
    this.onPointerEvent(PointerEventType.Scroll) {
        coroutineScope.launch {
            it.changes.forEach {
                it.consume()
                if (System.currentTimeMillis() - lastEventTime > 10) {
                    perform(it.scrollDelta.y, true)
                }
                lastEventTime = System.currentTimeMillis()
            }
        }
    }

