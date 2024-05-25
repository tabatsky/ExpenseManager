package jatx.expense.manager.presentation.viewmodel

import jatx.expense.manager.domain.models.*
import jatx.expense.manager.domain.usecase.*
import jatx.expense.manager.domain.util.dateOfMonthLastDayFromMonthKey
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.domain.util.utf8toCP1251
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ExpenseViewModel(
    private val saveExpenseTableToDBUseCase: SaveExpenseTableToDBUseCase,
    private val loadExpenseTableFromDBUseCase: LoadExpenseTableFromDBUseCase,
    private val loadXlsxUseCase: LoadXlsxUseCase,
    private val saveXlsxUseCase: SaveXlsxUseCase,
    private val updatePaymentUseCase: UpdatePaymentUseCase,
    private val insertPaymentUseCase: InsertPaymentUseCase,
    private val deletePaymentUseCase: DeletePaymentUseCase,
    private val renameCategoryUseCase: RenameCategoryUseCase,
    private val getCurrencyRateUseCase: GetCurrencyRateUseCase,
    private val coroutineScope: CoroutineScope
) {
    private val _currencyRates = MutableStateFlow<Map<String, Float>>(mapOf())

    private val _expenseTable: MutableStateFlow<ExpenseTable?> = MutableStateFlow(null)
    @OptIn(DelicateCoroutinesApi::class)
    val expenseTable = _expenseTable
        .combine(_currencyRates) { table, rates ->
            table?.copy(currencyRates = rates)
        }
        .stateIn(GlobalScope, SharingStarted.Eagerly, null)

    fun pieChartData(date: Date) = expenseTable.value?.pieChartData(date) ?: listOf()

    private val _currentExpenseEntry: MutableStateFlow<ExpenseEntry?> = MutableStateFlow(null)
    val currentExpenseEntry = _currentExpenseEntry.asStateFlow()

    private val _needShowXlsxChooserDialog = MutableStateFlow(false)
    val needShowXlsxChooserDialog = _needShowXlsxChooserDialog.asStateFlow()
    private val _isXlsxSaveDialog = MutableStateFlow(false)
    val isSaveDialog = _isXlsxSaveDialog.asStateFlow()
    private val _xlsxChooserDialogShowCounter = MutableStateFlow(0)
    val xlsxChooserDialogShowCounter = _xlsxChooserDialogShowCounter.asStateFlow()

    private val _needShowDatePickerDialog = MutableStateFlow(false)
    val needShowDatePickerDialog = _needShowDatePickerDialog.asStateFlow()
    private val _datePickerDate = MutableStateFlow(Date())
    val datePickerDate = _datePickerDate.asStateFlow()

    private val _currentPaymentEntry: MutableStateFlow<PaymentEntry?> = MutableStateFlow(null)
    val currentPaymentEntry = _currentPaymentEntry.asStateFlow()
    private val _newPaymentEntry: MutableStateFlow<PaymentEntry?> = MutableStateFlow(null)
    val newPaymentEntry = _newPaymentEntry.asStateFlow()

    private val _rowKeyToEdit: MutableStateFlow<RowKey?> = MutableStateFlow(null)
    val rowKeyToEdit = _rowKeyToEdit.asStateFlow()

    private val _showEditPaymentDialog = MutableStateFlow(false)
    val showEditPaymentDialog = _showEditPaymentDialog.asStateFlow()

    private val _needShowPieChartDialog = MutableStateFlow(false)
    val needShowPieChartDialog = _needShowPieChartDialog.asStateFlow()
    private val _pieChartMonthKey = MutableStateFlow(Date().monthKey)
    val pieChartMonthKey = _pieChartMonthKey.asStateFlow()

    fun onAppStart() {
        coroutineScope.launch {
            loadExpenseTableFromDBAndSaveToDefaultXlsx()
            _currencyRates.update {
                getCurrencyRateUseCase.execute()
            }
        }
    }

    fun loadXlsxToDB(xlsxPath: String) {
        coroutineScope.launch {
            loadXlsxUseCase
                .execute(xlsxPath)
                .let { tableXls ->
                    val tableDb = loadExpenseTableFromDBUseCase.execute()
                    val allCells = hashMapOf<CellKey, ExpenseEntry>()

                    tableXls.dates.forEach { date ->
                        tableXls.rowKeys.forEach { rowKey ->
                            val expenseEntryXls = tableXls.getCell(rowKey, date)
                            val expenseEntryDb = tableDb.getCell(rowKey, date)

                            if (expenseEntryDb.paymentSum == expenseEntryXls.paymentSum) {
                                val updatedExpenseEntryDb = expenseEntryDb
                                    .copy(
                                        rowKeyInt = rowKey.rowKeyInt,
                                        _payments = expenseEntryDb
                                            .payments
                                            .map {
                                                it.copy(rowKeyInt = rowKey.rowKeyInt)
                                            }
                                    )
                                allCells[CellKey(rowKey.cardName, rowKey.category, date.monthKey)] =
                                    updatedExpenseEntryDb
                            } else {
                                allCells[CellKey(rowKey.cardName, rowKey.category, date.monthKey)] =
                                    expenseEntryXls
                            }
                        }
                    }

                    ExpenseTable(allCells, tableXls.dates, tableXls.rowKeys)
                }
                .let {
                    saveExpenseTableToDBUseCase.execute(it)
                    loadExpenseTableFromDBAndSaveToDefaultXlsx()
                }
        }
    }

    fun saveXlsx(xlsxPath: String) {
        coroutineScope.launch {
            _expenseTable.value?.let {
                saveXlsxUseCase.execute(it, xlsxPath)
            }
        }
    }

    fun showStatisticsByComment() {
        expenseTable.value?.let {
            updateCurrentExpenseEntry(it.statisticsEntryByComment)
        }
    }

    fun showStatisticsByCategory() {
        expenseTable.value?.let {
            updateCurrentExpenseEntry(it.statisticsEntryByCategory)
        }
    }

    fun updateCurrentExpenseEntry(expenseEntry: ExpenseEntry) {
        with (expenseEntry) {
            println("$cardName $category $rowKeyInt ${date.formattedMonthAndYear}".utf8toCP1251())
        }
        _currentExpenseEntry.value = expenseEntry
    }

    fun showXlsxChooserDialog(show: Boolean, isSave: Boolean = false) {
        _isXlsxSaveDialog.value = isSave
        _needShowXlsxChooserDialog.value = show
        _xlsxChooserDialogShowCounter.value += 1
    }

    fun showDatePickerDialog(show: Boolean) {
        _needShowDatePickerDialog.value = show
    }

    fun setDatePickerDate(date: Date) {
        _datePickerDate.value = date
    }

    fun showRenameCategoryDialog(rowKey: RowKey?) {
        _rowKeyToEdit.value = rowKey
    }

    fun showEditPaymentDialog(paymentEntry: PaymentEntry?, show: Boolean) {
        paymentEntry?.takeIf { it.id >= 0 }?.let {
            _currentPaymentEntry.value = it
            _showEditPaymentDialog.value = show
        }
    }

    fun showPieChart() {
        showPieChartDialog(true)
    }

    fun showPieChartDialog(show: Boolean) {
        _needShowPieChartDialog.value = show
    }

    fun pieChartNextMonth() {
        _pieChartMonthKey.value += 1
    }

    fun pieChartPrevMonth() {
        _pieChartMonthKey.value -= 1
    }

    fun showAddPaymentDialog(show: Boolean) {
        currentExpenseEntry
            .value
            ?.takeIf { it.cardName.isNotEmpty() && it.category.isNotEmpty() }
            ?.let {
                val date = if (it.date.monthKey >= Date().monthKey) {
                    it.date
                } else {
                    it.date.monthKey.dateOfMonthLastDayFromMonthKey
                }
                _newPaymentEntry.value = PaymentEntry(
                    cardName = it.cardName,
                    category = it.category,
                    rowKeyInt = it.rowKeyInt,
                    date = date,
                    amount = 0,
                    comment = ""
                ).takeIf { show }
            }
    }

    fun updatePaymentEntryAtDBAndReloadExpenseTable(paymentEntry: PaymentEntry) {
        coroutineScope.launch {
            updatePaymentUseCase.execute(paymentEntry)
            loadExpenseTableFromDBAndSaveToDefaultXlsx()
        }
    }

    fun insertPaymentEntryIntoDBAndReloadExpenseTable(paymentEntry: PaymentEntry) {
        coroutineScope.launch {
            insertPaymentUseCase.execute(paymentEntry)
            loadExpenseTableFromDBAndSaveToDefaultXlsx()
        }
    }

    fun deletePaymentEntryFromDBAndReloadExpenseTable(paymentEntry: PaymentEntry) {
        coroutineScope.launch {
            deletePaymentUseCase.execute(paymentEntry)
            loadExpenseTableFromDBAndSaveToDefaultXlsx()
        }
    }

    fun renameCategoryAndReloadExpenseTable(newCategory: String, rowKey: RowKey) {
         coroutineScope.launch {
             renameCategoryUseCase.execute(newCategory, rowKey)
             loadExpenseTableFromDBAndSaveToDefaultXlsx()
         }
    }

    private suspend fun loadExpenseTableFromDBAndSaveToDefaultXlsx() {
        loadExpenseTableFromDBUseCase.execute().let {
            _expenseTable.value = it
            reloadCurrentExpenseEntry()
            saveXlsx(theDefaultXlsxPath)
        }
    }

    private fun reloadCurrentExpenseEntry() {
        currentExpenseEntry.value?.let {
            val rowKey = RowKey(it.cardName, it.category, it.rowKeyInt)
            val date = it.date
            val updatedExpenseEntry = _expenseTable.value
                ?.getCell(rowKey, date)
                ?.copy(currencyRates = _currencyRates.value)
            _currentExpenseEntry.value = updatedExpenseEntry
        }
    }
}