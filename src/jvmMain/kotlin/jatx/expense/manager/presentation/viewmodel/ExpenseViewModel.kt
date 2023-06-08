package jatx.expense.manager.presentation.viewmodel

import jatx.expense.manager.domain.models.*
import jatx.expense.manager.domain.usecase.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class ExpenseViewModel(
    private val saveExpenseTableToDBUseCase: SaveExpenseTableToDBUseCase,
    private val loadExpenseTableFromDBUseCase: LoadExpenseTableFromDBUseCase,
    private val loadXlsxUseCase: LoadXlsxUseCase,
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
    private val _xlsxChooserDialogShowCounter = MutableStateFlow(0)
    val xlsxChooserDialogShowCounter = _xlsxChooserDialogShowCounter.asStateFlow()

    private val _currentPaymentEntry: MutableStateFlow<PaymentEntry?> = MutableStateFlow(null)
    val currentPaymentEntry = _currentPaymentEntry.asStateFlow()
    private val _newPaymentEntry: MutableStateFlow<PaymentEntry?> = MutableStateFlow(null)
    val newPaymentEntry = _newPaymentEntry.asStateFlow()

    fun onAppStart() {
        coroutineScope.launch {
            loadExpenseTableFromDB()
        }
    }

    fun loadXlsxToDB(xlsxPath: String) {
        coroutineScope.launch {
            loadXlsxUseCase.execute(xlsxPath).collectLatest {
                saveExpenseTableToDBUseCase.execute(it)
                loadExpenseTableFromDB()
            }
        }
    }

    fun updateCurrentExpenseEntry(expenseEntry: ExpenseEntry) {
        _currentExpenseEntry.value = expenseEntry
    }

    fun showXlsxChooserDialog(show: Boolean) {
        _needShowXlsxChooserDialog.value = show
        _xlsxChooserDialogShowCounter.value += 1
    }

    fun showEditPaymentDialog(paymentEntry: PaymentEntry?) {
        _currentPaymentEntry.value = paymentEntry
    }

    fun showAddPaymentDialog(show: Boolean) {
        val expenseEntry = currentExpenseEntry.value ?: throw IllegalStateException("Current expense entry is null")
        _newPaymentEntry.value = PaymentEntry(
            cardName = expenseEntry.cardName,
            category = expenseEntry.category,
            rowKeyInt = expenseEntry.rowKeyInt,
            date = expenseEntry.date,
            amount = 0,
            comment = ""
        ).takeIf { show }
    }

    fun updatePaymentEntryToDB(paymentEntry: PaymentEntry) {
        coroutineScope.launch {
            updatePaymentUseCase.execute(paymentEntry)
            loadExpenseTableFromDB()
        }
    }

    fun insertPaymentEntryToDB(paymentEntry: PaymentEntry) {
        coroutineScope.launch {
            insertPaymentUseCase.execute(paymentEntry)
            loadExpenseTableFromDB()
        }
    }

    fun deletePaymentEntryFromDB(paymentEntry: PaymentEntry) {
        coroutineScope.launch {
            deletePaymentUseCase.execute(paymentEntry)
            loadExpenseTableFromDB()
        }
    }

    private suspend fun loadExpenseTableFromDB() {
        loadExpenseTableFromDBUseCase.execute().collectLatest {
            _expenseTable.value = it
            reloadCurrentExpenseEntry()
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