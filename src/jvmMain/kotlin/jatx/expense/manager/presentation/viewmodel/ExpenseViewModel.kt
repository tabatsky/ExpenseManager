package jatx.expense.manager.presentation.viewmodel

import jatx.expense.manager.domain.models.*
import jatx.expense.manager.domain.usecase.*
import jatx.expense.manager.domain.util.dateOfMonthLastDayFromMonthKey
import jatx.expense.manager.domain.util.monthKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
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
    private val coroutineScope: CoroutineScope
) {
    private val _expenseTable: MutableStateFlow<ExpenseTable?> = MutableStateFlow(null)
    val expenseTable = _expenseTable.asStateFlow()

    private val _currentExpenseEntry: MutableStateFlow<ExpenseEntry?> = MutableStateFlow(null)
    val currentExpenseEntry = _currentExpenseEntry.asStateFlow()

    private val _needShowXlsxChooserDialog = MutableStateFlow(false)
    val needShowXlsxChooserDialog = _needShowXlsxChooserDialog.asStateFlow()
    private val _isXlsxSaveDialog = MutableStateFlow(false)
    val isSaveDialog = _isXlsxSaveDialog.asStateFlow()
    private val _xlsxChooserDialogShowCounter = MutableStateFlow(0)
    val xlsxChooserDialogShowCounter = _xlsxChooserDialogShowCounter.asStateFlow()

    private val _currentPaymentEntry: MutableStateFlow<PaymentEntry?> = MutableStateFlow(null)
    val currentPaymentEntry = _currentPaymentEntry.asStateFlow()
    private val _newPaymentEntry: MutableStateFlow<PaymentEntry?> = MutableStateFlow(null)
    val newPaymentEntry = _newPaymentEntry.asStateFlow()

    private val _showEditPaymentDialog = MutableStateFlow(false)
    val showEditPaymentDialog = _showEditPaymentDialog.asStateFlow()

    fun onAppStart() {
        coroutineScope.launch {
            loadExpenseTableFromDBAndSaveToDefaultXlsx()
        }
    }

    fun loadXlsxToDB(xlsxPath: String) {
        coroutineScope.launch {
            loadXlsxUseCase
                .execute(xlsxPath)
                .combine(loadExpenseTableFromDBUseCase.execute()) { tableXls, tableDb ->
                    val allCells = hashMapOf<CellKey, ExpenseEntry>()

                    tableXls.dates.forEach { date ->
                        tableXls.rowKeys.forEach { rowKey ->
                            val expenseEntryXls = tableXls.getCell(rowKey, date)
                            val expenseEntryDb = tableDb.getCell(rowKey, date)

                            if (expenseEntryDb.paymentSum == expenseEntryXls.paymentSum) {
                                val updatedExpenseEntryDb = expenseEntryDb
                                    .copy(
                                        rowKeyInt = rowKey.rowKeyInt,
                                        payments = expenseEntryDb
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
                .collectLatest {
                    saveExpenseTableToDBUseCase.execute(it)
                    loadExpenseTableFromDBAndSaveToDefaultXlsx()
                }
        }
    }

    fun saveXlsx(xlsxPath: String) {
        coroutineScope.launch {
            expenseTable.value?.let {
                saveXlsxUseCase.execute(it, xlsxPath)
            }
        }
    }

    fun showStatistics() {
        expenseTable.value?.let {
            updateCurrentExpenseEntry(it.statisticsEntry)
        }
    }

    fun updateCurrentExpenseEntry(expenseEntry: ExpenseEntry) {
        _currentExpenseEntry.value = expenseEntry
    }

    fun showXlsxChooserDialog(show: Boolean, isSave: Boolean = false) {
        _isXlsxSaveDialog.value = isSave
        _needShowXlsxChooserDialog.value = show
        _xlsxChooserDialogShowCounter.value += 1
    }

    fun showEditPaymentDialog(paymentEntry: PaymentEntry?, show: Boolean) {
        paymentEntry?.takeIf { it.id >= 0 }?.let {
            _currentPaymentEntry.value = it
            _showEditPaymentDialog.value = show
        }
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

    private suspend fun loadExpenseTableFromDBAndSaveToDefaultXlsx() {
        loadExpenseTableFromDBUseCase.execute().collectLatest {
            _expenseTable.value = it
            reloadCurrentExpenseEntry()
            saveXlsx(theDefaultXlsxPath)
        }
    }

    private fun reloadCurrentExpenseEntry() {
        currentExpenseEntry.value?.let {
            val rowKey = RowKey(it.cardName, it.category, it.rowKeyInt)
            val date = it.date
            val updatedExpenseEntry = expenseTable.value?.getCell(rowKey, date)
            _currentExpenseEntry.value = updatedExpenseEntry
        }
    }
}