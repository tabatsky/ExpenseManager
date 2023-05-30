package jatx.expense.manager.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import jatx.expense.manager.data.utf8toCP1251
import jatx.expense.manager.domain.models.ExpenseEntry
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.presentation.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExpenseTable(expenseViewModel: ExpenseViewModel) {
    val parsedXlsx by expenseViewModel.parsedXlsx.collectAsState()

    val firstCellWidth = 90.dp
    val secondCellWidth = 150.dp
    val cellWidth = 70.dp
    val cellHeight = 36.dp

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
                    Text(
                        modifier = Modifier.width(firstCellWidth).height(cellHeight),
                        text = ""
                    )
                    Text(
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
                            Text(
                                modifier = Modifier
                                    .width(firstCellWidth)
                                    .height(cellHeight)
                                    .background(colorByKey(rowKey.third)),
                                text = rowKey.first.utf8toCP1251()
                            )
                            Text(
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
                                Text(
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
                                                    date,
                                                    0.0
                                                )
                                        Text(
                                            modifier = Modifier
                                                .width(cellWidth)
                                                .height(cellHeight)
                                                .background(colorByKey(rowKey.third)),
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

val cellColors = listOf(
    Color(0xFFAAAAFF),
    Color(0xFFCCAAFF),
    Color(0xFFAAFFAA)
)

fun colorByKey(key: Int): Color {
    val key1 = key / 1000 - 1
    return cellColors.getOrNull(key1) ?: Color.White
}