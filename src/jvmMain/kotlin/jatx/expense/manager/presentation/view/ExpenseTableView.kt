package jatx.expense.manager.presentation.view

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextAlign
import jatx.expense.manager.di.Injector
import jatx.expense.manager.domain.models.*
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.domain.util.utf8toCP1251
import jatx.expense.manager.presentation.viewmodel.ExpenseViewModel
import jatx.expense.manager.res.*
import kotlinx.coroutines.launch

const val fixedRowCount = 1

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExpenseTable() {
    val expenseViewModel = Injector.expenseViewModel

    val expenseTable by expenseViewModel.expenseTable.collectAsState()

    val rowListState = rememberLazyListState()

    expenseTable?.let { theExpenseTable ->
        val coroutineScope = rememberCoroutineScope()
        val firstColumnListState = rememberLazyListState()
        val columnListState = rememberLazyListState()
        val firstColumnScrollState = rememberScrollState()
        val columnScrollState = rememberScrollState()

        var scrollY by remember { mutableStateOf(0) }

        suspend fun syncScroll(minusDelta: Float, isMouse: Boolean) {
            scrollY += if (isMouse) minusDelta.toInt() * 20 else minusDelta.toInt()
            firstColumnScrollState.scrollTo(scrollY)
            columnScrollState.scrollTo(scrollY)
        }

        Row {
            Column {
                Row {
                    ExpenseCell(
                        modifier = Modifier.width(firstCellWidth).height(cellHeight),
                        text = ""
                    )
                    ExpenseCell(
                        modifier = Modifier.width(secondCellWidth).height(cellHeight),
                        text = ""
                    )
                }
                theExpenseTable.rowKeysWithTotals.take(fixedRowCount).forEach { rowKey ->
                    Row {
                        FirstTwoColumnsRow(rowKey)
                    }
                }
                LazyColumn(
                    state = firstColumnListState,
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
                        .verticalScroll(firstColumnScrollState)
                        .height(cellHeight * theExpenseTable.rowKeysWithTotals.size)
                ) {
                    items(theExpenseTable.rowKeysWithTotals.drop(fixedRowCount)) { rowKey ->
                        FirstTwoColumnsRow(rowKey)
                    }
                }
            }

            LazyRow(
                state = rowListState,
                modifier = Modifier
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            coroutineScope.launch {
                                rowListState.scrollBy(-delta)
                            }
                        }
                    )
            ) {
                item {
                    Column {
                        Row {
                            theExpenseTable.datesWithZeroDate.forEach { date ->
                                ExpenseCell(
                                    modifier = Modifier.width(cellWidth).height(cellHeight),
                                    text = date.formattedMonthAndYear.utf8toCP1251()
                                )
                            }
                        }

                        theExpenseTable.rowKeysWithTotals.take(fixedRowCount).forEach { rowKey ->
                            Row {
                                CommonRow(rowKey, theExpenseTable, expenseViewModel)
                            }
                        }

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
                                .onPointerEvent(PointerEventType.Scroll) {
                                    coroutineScope.launch {
                                        syncScroll(it.changes.first().scrollDelta.y, true)
                                    }
                                }
                                .verticalScroll(columnScrollState)
                                .height(cellHeight * theExpenseTable.rowKeysWithTotals.size)
                                .fillMaxWidth()
                        ) {
                            items(theExpenseTable.rowKeysWithTotals.drop(fixedRowCount)) { rowKey ->
                                CommonRow(rowKey, theExpenseTable, expenseViewModel)
                            }
                        }
                    }
                }
            }
        }
    }


    val launchedEffectKey =
        (expenseTable?.datesWithZeroDate?.maxOfOrNull { it.monthKey } ?: 0) +
                (expenseTable?.rowKeysWithTotals?.maxOfOrNull { it.rowKeyInt } ?: 0)

    LaunchedEffect(launchedEffectKey) {
        rowListState.scrollBy(500f * (expenseTable?.datesWithZeroDate?.size ?: 0))
    }
}

@Composable
fun FirstTwoColumnsRow(rowKey: RowKey) {
    Row {
        ExpenseCell(
            modifier = Modifier
                .width(firstCellWidth)
                .height(cellHeight)
                .background(colorByKey(rowKey.rowKeyInt)),
            text = rowKey.cardName.utf8toCP1251()
        )
        ExpenseCell(
            modifier = Modifier
                .width(secondCellWidth)
                .height(cellHeight)
                .background(colorByKey(rowKey.rowKeyInt)),
            text = rowKey.category.utf8toCP1251()
        )
    }
}

@Composable
fun CommonRow(
    rowKey: RowKey,
    theExpenseTable: ExpenseTable,
    expenseViewModel: ExpenseViewModel
) {
    Row {
        theExpenseTable.datesWithZeroDate.forEach { date ->
            val expenseEntry =
                theExpenseTable.getCell(rowKey, date)
            ExpenseCell(
                modifier = Modifier
                    .width(cellWidth)
                    .height(cellHeight)
                    .background(colorByKey(rowKey.rowKeyInt))
                    .clickable {
                        expenseViewModel.updateCurrentExpenseEntry(expenseEntry)
                    },
                text = expenseEntry.paymentSum.toString()
            )
        }
    }
}

@Composable
fun ExpenseCell(modifier: Modifier, text: String) {
    Box(
        modifier = modifier
            .border(BorderStroke(borderWidth, blackColor)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontSize = commonFontSize
        )
    }
}

val cellColors = listOf(
    blueColor,
    violetColor,
    greenColor
)

fun colorByKey(key: Int): Color {
    val categoryKey = key.categoryKey
    if (categoryKey == lohKey) return redColor
    val cardNameKey = key.cardNameKey
    return cellColors.getOrNull(cardNameKey - 1) ?: whiteColor
}