package jatx.expense.manager.presentation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import jatx.expense.manager.di.appComponent
import jatx.expense.manager.res.datePickerDialogHeight
import jatx.expense.manager.res.datePickerDialogWidth
import java.time.LocalDate
import java.time.YearMonth
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun DatePickerDialogWrapper() {
    val expenseViewModel = appComponent.expenseViewModel

    val show by expenseViewModel.needShowDatePickerDialog.collectAsState()
    val date by expenseViewModel.datePickerDate.collectAsState()

    fun close() {
        expenseViewModel.showDatePickerDialog(false)
    }

    fun setDateAndClose(newDate: Date) {
        expenseViewModel.setDatePickerDate(newDate)
        close()
    }

    if (show) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.time + offset
        )
        Dialog(
            onDismissRequest = { close() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Column(
                modifier = Modifier
                    .width(datePickerDialogWidth)
                    .height(datePickerDialogHeight)
            ) {
                CustomDatePicker(
                    state = datePickerState,
                    onApply = {
                        datePickerState.selectedDateMillis?.let {
                            val date = Date(it)
                            println(date)
                            setDateAndClose(date)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    state: DatePickerState,
    modifier: Modifier = Modifier,
    columnSpacing: Int = 12,  // 👈 Настраиваемое расстояние между столбцами
    showYearColumn: Boolean = true,
    showMonthColumn: Boolean = true,
    showDayColumn: Boolean = true,
    onApply: () -> Unit
) {
    val selectedDate by remember {
        derivedStateOf {
            state.selectedDateMillis?.let { millis ->
                LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
            }
        }
    }

    // Получаем текущий год, месяц, день
    var currentYear by remember { mutableIntStateOf(2024) }
    var currentMonth by remember { mutableIntStateOf(1) }
    var currentDay by remember { mutableIntStateOf(1) }

    LaunchedEffect(selectedDate) {
        selectedDate?.let { date ->
            currentYear = date.year
            currentMonth = date.monthValue
            currentDay = date.dayOfMonth
        }
    }

    // Генерация списков
    val years = (1900..2100).toList()
    val months = (1..12).toList()
    val maxDays = remember(currentYear, currentMonth) {
        YearMonth.of(currentYear, currentMonth).lengthOfMonth()
    }
    val days = (1..maxDays).toList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f),
            horizontalArrangement = Arrangement.spacedBy(columnSpacing.dp) // 👈 Расстояние между столбцами
        ) {
            // Колонка ГОД
            if (showYearColumn) {
                CustomColumn(
                    title = "Год",
                    items = years,
                    selectedValue = currentYear,
                    onItemSelected = { year ->
                        currentYear = year
                        state.setSelection(year, currentMonth, currentDay)
                    }
                )
            }

            // Колонка МЕСЯЦ
            if (showMonthColumn) {
                CustomColumn(
                    title = "Месяц",
                    items = months,
                    selectedValue = currentMonth,
                    formatItem = { month -> String.format("%02d", month) },
                    onItemSelected = { month ->
                        currentMonth = month
                        state.setSelection(currentYear, month, currentDay)
                    }
                )
            }

            // Колонка ДЕНЬ
            if (showDayColumn) {
                CustomColumn(
                    title = "День",
                    items = days,
                    selectedValue = currentDay,
                    formatItem = { day -> String.format("%02d", day) },
                    onItemSelected = { day ->
                        currentDay = day
                        state.setSelection(currentYear, currentMonth, day)
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Button(
                onClick = {
                    onApply()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text("Выбрать")
            }
        }
    }
}

@Composable
fun <T> RowScope.CustomColumn(
    title: String,
    items: List<T>,
    selectedValue: T,
    formatItem: (T) -> String = { it.toString() },
    onItemSelected: (T) -> Unit
) {
    // Состояние для управления прокруткой
    val listState = rememberLazyListState()

    // Находим индекс выбранного элемента
    val selectedIndex by remember(items, selectedValue) {
        derivedStateOf {
            items.indexOf(selectedValue).coerceAtLeast(0)
        }
    }

    // Автоматическая прокрутка к выбранному элементу
    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 1) {
            listState.scrollToItem(
                index = selectedIndex - 1,
                scrollOffset = 0
            )
        } else {
            listState.scrollToItem(
                index = 0,
                scrollOffset = 0
            )
        }
    }

    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                val isSelected = item == selectedValue

                TextButton(
                    onClick = { onItemSelected(item) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = formatItem(item),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// Расширение для DatePickerState для установки даты
@OptIn(ExperimentalMaterial3Api::class)
fun DatePickerState.setSelection(year: Int, month: Int, day: Int) {
    val localDate = LocalDate.of(year, month, day)
    val millis = localDate.toEpochDay() * (24 * 60 * 60 * 1000)
    val delta = (selectedDateMillis ?: 0) % (24 * 60 * 60 * 1000)
    selectedDateMillis = millis + delta
}

// Расширение для получения выбранной даты
@OptIn(ExperimentalMaterial3Api::class)
fun DatePickerState.getSelectedDate(): LocalDate? {
    return selectedDateMillis?.let { millis ->
        LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
    }
}