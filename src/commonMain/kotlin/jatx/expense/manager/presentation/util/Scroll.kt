package jatx.expense.manager.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope

@Composable
expect fun Modifier.onScroll(coroutineScope: CoroutineScope, perform: suspend (Float, Boolean) -> Unit): Modifier