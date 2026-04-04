package jatx.expense.manager.presentation.viewmodel

import jatx.expense.manager.data.backup.loadFromFirestoreOnAppStart
import jatx.expense.manager.data.backup.saveToFirestoreOnAppFinish
import jatx.expense.manager.data.db.AppDatabase
import jatx.expense.manager.data.firebase.FirebaseAuthData
import jatx.expense.manager.data.firebase.FirebaseConfig
import jatx.expense.manager.data.firebase.firebaseAuth
import jatx.expense.manager.data.firebase.initFirebase
import jatx.expense.manager.data.firebase.loadDataFromFirestore
import jatx.expense.manager.data.firebase.saveDataToFirestore
import jatx.expense.manager.data.xlsx.saveToDefaultXlsx
import jatx.expense.manager.di.AndroidContextProvider
import jatx.expense.manager.di.AppScope
import jatx.expense.manager.domain.models.*
import jatx.expense.manager.domain.usecase.*
import jatx.expense.manager.domain.util.dateOfMonthLastDayFromMonthKey
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.domain.util.utf8toCP1251
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import java.util.*
import kotlin.collections.map

@AppScope
@Inject
class ExpenseViewModel(
    private val saveExpenseTableToDBUseCase: SaveExpenseTableToDBUseCase,
    private val loadExpenseTableFromDBUseCase: LoadExpenseTableFromDBUseCase,
    private val loadXlsxUseCase: LoadXlsxUseCase,
    private val saveXlsxUseCase: SaveXlsxUseCase,
    private val saveTxtUseCase: SaveTxtUseCase,
    private val updatePaymentUseCase: UpdatePaymentUseCase,
    private val insertPaymentUseCase: InsertPaymentUseCase,
    private val deletePaymentUseCase: DeletePaymentUseCase,
    private val renameCategoryUseCase: RenameCategoryUseCase,
    private val swapRowKeysIntUseCase: SwapRowKeysIntUseCase,
    private val getCurrencyRateUseCase: GetCurrencyRateUseCase,
    private val selectAllUseCase: SelectAllUseCase,
    private val appDatabase: AppDatabase,
    private val coroutineScope: CoroutineScope,

    private val androidContextProvider: AndroidContextProvider?
) {
    private val _currencyRates = MutableStateFlow<Map<String, Float>>(mapOf())

    private val _expenseTable: MutableStateFlow<ExpenseTable?> = MutableStateFlow(null)
    val expenseTable = _expenseTable
        .combine(_currencyRates) { table, rates ->
            println(rates)
            println(table?.version ?: 0)
            table?.copy(currencyRates = rates, version = table.version.plus(1))
        }
        .stateIn(coroutineScope, SharingStarted.Eagerly, null)

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

    private val _needShowEditPaymentDialog = MutableStateFlow(false)
    val needShowEditPaymentDialog = _needShowEditPaymentDialog.asStateFlow()

    private val _needShowPieChartDialog = MutableStateFlow(false)
    val needShowPieChartDialog = _needShowPieChartDialog.asStateFlow()
    private val _needShowPieChartByCommentDialog = MutableStateFlow(false)
    val needShowPieChartByCommentDialog = _needShowPieChartByCommentDialog.asStateFlow()
    private val _needShowPieChartByCommentMinusDialog = MutableStateFlow(false)
    val needShowPieChartByCommentMinusDialog = _needShowPieChartByCommentMinusDialog.asStateFlow()
    private val _pieChartFilter = MutableStateFlow("")
    val pieChartFilter = _pieChartFilter.asStateFlow()
    private val _pieChartMonthKey = MutableStateFlow(Date().monthKey)
    val pieChartMonthKey = _pieChartMonthKey.asStateFlow()
    private val _pieChartMonthKey2 = MutableStateFlow<Int?>(null)
    val pieChartMonthKey2 = _pieChartMonthKey2.asStateFlow()
    private val _pieChartShowSkipped = MutableStateFlow(false)
    val pieChartShowSkipped = _pieChartShowSkipped.asStateFlow()
    private val _pieChartJoinByCards = MutableStateFlow(false)
    val pieChartJoinByCards = _pieChartJoinByCards.asStateFlow()
    private val _pieChartFullComments = MutableStateFlow(false)
    val pieChartFullComments = _pieChartFullComments.asStateFlow()

    private val _needShowProgressDialog = MutableStateFlow(false)
    val needShowProgressDialog = _needShowProgressDialog.asStateFlow()

    fun pieChartData(date: Date, date2: Date? = null, showSkipped: Boolean, joinByCards: Boolean) = expenseTable.value?.pieChartData(date, date2, showSkipped, joinByCards) ?: listOf()
    fun overallPieChartData(showSkipped: Boolean, joinByCards: Boolean) = expenseTable.value?.overallPieChartData(showSkipped, joinByCards) ?: listOf()

    fun pieChartDataByComment(date: Date, date2: Date? = null, filter: String = "", fullComments: Boolean = false) = expenseTable.value?.pieChartDataByComment(date, date2, filter, fullComments) ?: listOf()
    fun overallPieChartDataByComment(filter: String = "", fullComments: Boolean = false) = expenseTable.value?.overallPieChartDataByComment(filter, fullComments) ?: listOf()

    fun pieChartDataByCommentMinus(date: Date, date2: Date? = null) = expenseTable.value?.pieChartDataByCommentMinus(date, date2) ?: listOf()
    fun overallPieChartDataByCommentMinus() = expenseTable.value?.overallPieChartDataByCommentMinus() ?: listOf()



    private val _needShowByMonthChartDialog = MutableStateFlow(false)
    val needShowByMonthChartDialog = _needShowByMonthChartDialog.asStateFlow()

    fun byMonthData(filter: String = "") = expenseTable
        .map {
            it?.byMonthData(filter) ?: listOf()
        }

    fun onAppStart(firebaseConfig: FirebaseConfig, firebaseAuthData: FirebaseAuthData) {
        coroutineScope.launch {
            showProgressDialog(true)
            try {
                initFirebase(firebaseConfig, androidContextProvider)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            firebaseAuth(firebaseAuthData)
            if (loadFromFirestoreOnAppStart) {
                loadDataFromFirestore()?.let {
                    saveExpenseTableToDBUseCase.execute(it)
                }
            }
            loadExpenseTableFromDB(saveToDefaultXlsx)
            _currencyRates.update {
                getCurrencyRateUseCase.execute()
            }
            showProgressDialog(false)
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

                    val allCellsWithoutPaymentId = allCells
                        .map { entry ->
                            val paymentsWithoutId = entry.value.payments.map {
                                it.copy(id = 0)
                            }
                            val valueWithoutPaymentId = entry.value.copy(_payments = paymentsWithoutId)
                            entry.key to valueWithoutPaymentId
                        }
                        .toMap()
                    ExpenseTable(allCellsWithoutPaymentId, tableXls.dates, tableXls.rowKeys)
                }
                .let {
                    saveExpenseTableToDBUseCase.execute(it)
                    loadExpenseTableFromDB(saveToDefaultXlsx)
                }
        }
    }

    private fun saveXlsx(xlsxPath: String) {
        _expenseTable.value?.let {
            saveXlsxUseCase.execute(it, xlsxPath)
        }
    }

    fun saveXlsxWithCoroutine(xlsxPath: String) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                saveXlsx(xlsxPath)
            }
        }
    }

    fun saveCurrentToTxt() {
        currentExpenseEntry.value?.let {
            saveTxtUseCase.execute(
                it.payments,
                it.cardName,
                it.category,
                it.date
            )
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
            _needShowEditPaymentDialog.value = show
        }
    }

    fun showPieChart() {
        showPieChartDialog(true)
    }

    fun showPieChartDialog(show: Boolean) {
        _needShowPieChartDialog.value = show
    }

    fun showPieChartByComment() {
        showPieChartByCommentDialog(true)
    }

    fun showPieChartByCommentDialog(show: Boolean) {
        _needShowPieChartByCommentDialog.value = show
    }

    fun showPieChartByCommentMinus() {
        showPieChartByCommentMinusDialog(true)
    }

    fun showPieChartByCommentMinusDialog(show: Boolean) {
        _needShowPieChartByCommentMinusDialog.value = show
    }

    fun updatePieChartShowSkipped(show: Boolean) {
        _pieChartShowSkipped.value = show
    }

    fun updatePieChartJoinByCards(join: Boolean) {
        _pieChartJoinByCards.value = join
    }

    fun updatePieChartFullComments(full: Boolean) {
        _pieChartFullComments.value = full
    }

    fun pieChartUpdateFilter(filter: String) {
        _pieChartFilter.value = filter
    }

    fun pieChartNextMonth() {
        _pieChartMonthKey.value += 1
    }

    fun pieChartPrevMonth() {
        _pieChartMonthKey.value -= 1
    }

    fun pieChartNextMonth2() {
        val newValue = pieChartMonthKey2.value?.plus(1)?.takeIf { it <= Date().monthKey }
        _pieChartMonthKey2.value = newValue
    }

    fun pieChartPrevMonth2() {
        val newValue = pieChartMonthKey2.value?.minus(1) ?: Date().monthKey
        _pieChartMonthKey2.value = newValue
    }

    fun showByMonthChart() {
        showByMonthChartDialog(true)
    }

    fun showByMonthChartDialog(show: Boolean) {
        _needShowByMonthChartDialog.value = show
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
            loadExpenseTableFromDB(saveToDefaultXlsx)
        }
    }

    fun insertPaymentEntryIntoDBAndReloadExpenseTable(paymentEntry: PaymentEntry) {
        coroutineScope.launch {
            insertPaymentUseCase.execute(paymentEntry)
            loadExpenseTableFromDB(saveToDefaultXlsx)
        }
    }

    fun deletePaymentEntryFromDBAndReloadExpenseTable(paymentEntry: PaymentEntry) {
        coroutineScope.launch {
            deletePaymentUseCase.execute(paymentEntry)
            loadExpenseTableFromDB(saveToDefaultXlsx)
        }
    }

    fun renameCategoryAndReloadExpenseTable(newCategory: String, rowKey: RowKey) {
         coroutineScope.launch {
             renameCategoryUseCase.execute(newCategory, rowKey)
             loadExpenseTableFromDB(saveToDefaultXlsx)
         }
    }

    fun swapRowKeysIntAndReloadExpenseTable(rowKeyInt1: Int, rowKeyInt2: Int) {
        coroutineScope.launch {
            swapRowKeysIntUseCase.execute(rowKeyInt1, rowKeyInt2)
            loadExpenseTableFromDB(saveToDefaultXlsx)
        }
    }

    private suspend fun loadExpenseTableFromDB(saveToDefaultXlsx: Boolean) = withContext(Dispatchers.IO)  {
        loadExpenseTableFromDBUseCase.execute().let {
            _expenseTable.value = it
            reloadCurrentExpenseEntry()
            if (saveToDefaultXlsx) {
                saveXlsx(theDefaultXlsxPath)
            }
        }
    }

    private fun reloadCurrentExpenseEntry() {
        currentExpenseEntry.value?.let {
            val rowKey = RowKey(it.cardName, it.category, it.rowKeyInt)
            val date = it.date
            val updatedExpenseEntry = _expenseTable.value
                ?.getCell(rowKey, date)
                ?.copy(currencyRates = _currencyRates.value)
            updatedExpenseEntry?.let {
                updateCurrentExpenseEntry(it)
            }
        }
    }

    fun showProgressDialog(show: Boolean) {
        _needShowProgressDialog.value = show
    }

    fun onAppExit(after: () -> Unit) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                showProgressDialog(true)
                if (saveToFirestoreOnAppFinish) {
                    saveDataToFirestore(selectAllUseCase.execute())
                }
                appDatabase.close()
                showProgressDialog(false)
                after()
            }
        }
    }
}