package jatx.expense.manager.presentation.view

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.util.formattedForPaymentList
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.platform.isAndroid
import jatx.expense.manager.presentation.menu.DropdownMenuWrapper
import jatx.expense.manager.presentation.util.onScroll
import jatx.expense.manager.res.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.sign

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PaymentListView() {
    val expenseViewModel = appComponent.expenseViewModel

    val expenseEntry by expenseViewModel.currentExpenseEntry.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val columnListState = rememberLazyListState()

    var scrollY by remember { mutableFloatStateOf(0f) }

    val itemHeight = with (LocalDensity.current) {
        cellHeight.toPx()
    }
    println(itemHeight)

    suspend fun syncScroll(minusDelta: Float, isMouse: Boolean) = withContext(Dispatchers.Main) {
        if (isMouse) {
            scrollY += minusDelta.toInt().sign * 24 * itemHeight
            println("sync scroll to: $scrollY")
        } else {
            scrollY += minusDelta
        }
        if (abs(scrollY) > itemHeight * 0.2f) {
            println("sync scroll by: $scrollY")
            val scrollBy = scrollY
            scrollY = 0f
            columnListState.scrollBy(scrollBy)
        }
    }

    (expenseEntry?.payments ?: listOf()).let {
        Column {
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)) {
                LazyColumn(
                    state = columnListState,
                    modifier = Modifier
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                coroutineScope.launch {
                                    syncScroll(-delta, false)
                                }
                            }
                        )
                        .onScroll(coroutineScope) { delta, isMouse ->
                            syncScroll(delta, isMouse)
                        }
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    items(it.reversed()) { paymentEntry ->
                        PaymentItem(paymentEntry)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Button(
                    modifier = Modifier
                        .weight(1.0f)
                        .height(buttonHeight),
                    onClick = {
                        expenseViewModel.showAddPaymentDialog(true)
                    }
                ) {
                    Text(
                        text = buttonAddLabel,
                        textAlign = TextAlign.Center,
                        fontSize = buttonFontSize
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(0.2f)
                        .height(buttonHeight)
                )
                if (isAndroid) {
                    DropdownMenuWrapper(appComponent.menuCallbacks)
                    Button(
                        modifier = Modifier
                            .weight(1.0f)
                            .height(buttonHeight),
                        onClick = {
                            expenseViewModel.showDropdownMenu(true)
                        }
                    ) {
                        Text(
                            text = menuLabel,
                            textAlign = TextAlign.Center,
                            fontSize = buttonFontSize
                        )
                    }
                } else {
                    Button(
                        modifier = Modifier
                            .weight(1.0f)
                            .height(buttonHeight),
                        onClick = {
                            expenseViewModel.saveCurrentToTxt()
                        }
                    ) {
                        Text(
                            text = buttonSaveToTxtLabel,
                            textAlign = TextAlign.Center,
                            fontSize = buttonFontSize
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentItem(paymentEntry: PaymentEntry) {
    println("payment: ${paymentEntry.amountStr} ${paymentEntry.category}".utf8toCP1251())

    val expenseViewModel = appComponent.expenseViewModel

    Column(
        modifier = Modifier
            .border(BorderStroke(1.dp, Color.Black))
            .background(yellowColor)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .clickable {
                expenseViewModel.showEditPaymentDialog(paymentEntry, true)
            }
    ) {
        Text(text = paymentEntry.amountStr)
        Text(text = paymentEntry.comment)
        Text(text = paymentEntry.date.formattedForPaymentList)
    }
}