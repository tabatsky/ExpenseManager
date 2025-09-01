package jatx.expense.manager.presentation.view

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.util.formattedForPaymentList
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.res.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PaymentListView() {
    val expenseViewModel = appComponent.expenseViewModel

    val expenseEntry by expenseViewModel.currentExpenseEntry.collectAsState()

    expenseEntry?.let {
        val coroutineScope = rememberCoroutineScope()
        val columnScrollState = rememberScrollState()

        var scrollY by remember { mutableStateOf(0) }

        suspend fun syncScroll(minusDelta: Float, isMouse: Boolean) {
            scrollY += if (isMouse) minusDelta.toInt() * 20 else minusDelta.toInt()
            columnScrollState.scrollTo(scrollY)
        }


        Column {
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)) {
                LazyColumn(
                    modifier = Modifier
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                coroutineScope.launch {
                                    syncScroll(-delta, false)
                                }
                            }
                        )
                        .onPointerEvent(PointerEventType.Scroll) {
                            coroutineScope.launch {
                                syncScroll(it.changes.first().scrollDelta.y, true)
                            }
                        }
                        .verticalScroll(columnScrollState)
                        .wrapContentHeight()
                        .heightIn(min = 0.dp, max = paymentCellHeight * it.payments.size * 2f)
                ) {
                    items(it.payments.reversed()) { paymentEntry ->
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