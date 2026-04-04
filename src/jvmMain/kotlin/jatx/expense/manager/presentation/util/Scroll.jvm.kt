package jatx.expense.manager.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun Modifier.onScroll(coroutineScope: CoroutineScope, perform: suspend (Float, Boolean) -> Unit) = this
    .onPointerEvent(PointerEventType.Scroll) {
        it.changes.forEach { it.consume() }
        coroutineScope.launch {
            it.changes.forEach {
                perform(it.scrollDelta.y, true)
            }
        }
    }