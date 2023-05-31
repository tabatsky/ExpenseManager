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
import jatx.expense.manager.data.lohKey
import jatx.expense.manager.data.utf8toCP1251
import jatx.expense.manager.domain.models.ExpenseEntry
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.presentation.res.*
import jatx.expense.manager.presentation.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExpenseTable(expenseViewModel: ExpenseViewModel) {
    val parsedXlsx by expenseViewModel.parsedXlsx.collectAsState()

    parsedXlsx?.let { theParsedXlsx ->
        val coroutineScope = rememberCoroutineScope()
        val rowScrollState = rememberLazyListState()
        val firstColumnListState = rememberLazyListState()
        val columnListState = rememberLazyListState()
        val firstColumnScrollState = rememberScrollState()
        val columnScrollState = rememberScrollState()

        var scrollY by remember { mutableStateOf(0) }

        suspend fun syncScroll(minusDelta: Float, isMouse: Boolean) {
            println(minusDelta)
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
                        .height(cellHeight * theParsedXlsx.allRowKeys.size)
                ) {
                    items(theParsedXlsx.allRowKeys) {rowKey ->
                        Row {
                            ExpenseCell(
                                modifier = Modifier
                                    .width(firstCellWidth)
                                    .height(cellHeight)
                                    .background(colorByKey(rowKey.third)),
                                text = rowKey.first.utf8toCP1251()
                            )
                            ExpenseCell(
                                modifier = Modifier
                                    .width(secondCellWidth)
                                    .height(cellHeight)
                                    .background(colorByKey(rowKey.third)),
                                text = rowKey.second.utf8toCP1251()
                            )
                        }
                    }
                }
            }

            LazyRow(
                state = rowScrollState,
                modifier = Modifier
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            coroutineScope.launch {
                                rowScrollState.scrollBy(-delta)
                            }
                        }
                    )
            ) {
                item {
                    Column {
                        Row {
                            theParsedXlsx.allDates.forEach { date ->
                                ExpenseCell(
                                    modifier = Modifier.width(cellWidth).height(cellHeight),
                                    text = date.formattedMonthAndYear
                                )
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
                                .height(cellHeight * theParsedXlsx.allRowKeys.size)
                                .fillMaxWidth()
                        ) {
                            items(theParsedXlsx.allRowKeys) { rowKey ->
                                Row {
                                    theParsedXlsx.allDates.forEach { date ->
                                        val expenseEntry =
                                            theParsedXlsx.allCells[Triple(
                                                rowKey.first,
                                                rowKey.second,
                                                date.monthKey
                                            )]
                                                ?: ExpenseEntry.makeFromDouble(
                                                    rowKey.first,
                                                    rowKey.second,
                                                    rowKey.third,
                                                    date,
                                                    0.0
                                                )
                                        ExpenseCell(
                                            modifier = Modifier
                                                .width(cellWidth)
                                                .height(cellHeight)
                                                .background(colorByKey(rowKey.third))
                                                .clickable {
                                                    expenseViewModel.updateCurrentExpenseEntry(expenseEntry)
                                                },
                                            text = expenseEntry.paymentSum.toString()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
        )
    }
}

val cellColors = listOf(
    blueColor,
    violetColor,
    greenColor
)

fun colorByKey(key: Int): Color {
    val key2 = (key - 1) % 1000
    if (key2 == lohKey) return redColor
    val key1 = key / 1000 - 1
    return cellColors.getOrNull(key1) ?: whiteColor
}