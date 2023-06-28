package jatx.expense.manager.di

import jatx.expense.manager.data.db.DatabaseDriverFactory
import jatx.expense.manager.data.repository.PaymentRepositoryImpl
import jatx.expense.manager.data.xlsx.XlsxParserFactoryImpl
import jatx.expense.manager.data.xlsx.XlsxSaverFactoryImpl
import jatx.expense.manager.db.AppDatabase
import jatx.expense.manager.domain.repository.PaymentRepository
import jatx.expense.manager.domain.usecase.*
import jatx.expense.manager.domain.xlsx.XlsxParserFactory
import jatx.expense.manager.domain.xlsx.XlsxSaverFactory
import jatx.expense.manager.presentation.menu.MenuCallbacks
import jatx.expense.manager.presentation.viewmodel.ExpenseViewModel
import kotlinx.coroutines.CoroutineScope

class Injector(
    databaseDriverFactory: DatabaseDriverFactory,
    coroutineScope: CoroutineScope
) {
    private val driver = databaseDriverFactory.createDriver()
    private val appDatabase = AppDatabase.invoke(driver)
    private val paymentRepository: PaymentRepository =
        PaymentRepositoryImpl(appDatabase)
    private val xlsxParserFactory: XlsxParserFactory =
        XlsxParserFactoryImpl()
    private val xlsxSaverFactory: XlsxSaverFactory =
        XlsxSaverFactoryImpl()
    private val saveExpenseTableToDBUseCase =
        SaveExpenseTableToDBUseCase(paymentRepository)
    private val loadExpenseTableFromDBUseCase =
        LoadExpenseTableFromDBUseCase(paymentRepository)
    private val loadXlsxUseCase =
        LoadXlsxUseCase(xlsxParserFactory)
    private val saveXlsxUseCase =
        SaveXlsxUseCase(xlsxSaverFactory)
    private val updatePaymentUseCase =
        UpdatePaymentUseCase(paymentRepository)
    private val insertPaymentUseCase =
        InsertPaymentUseCase(paymentRepository)
    private val deletePaymentUseCase =
        DeletePaymentUseCase(paymentRepository)
    private val expenseViewModel =
        ExpenseViewModel(
            saveExpenseTableToDBUseCase,
            loadExpenseTableFromDBUseCase,
            loadXlsxUseCase,
            saveXlsxUseCase,
            updatePaymentUseCase,
            insertPaymentUseCase,
            deletePaymentUseCase,
            coroutineScope
        )
    private val menuCallbacks = MenuCallbacks()

    companion object {
        private lateinit var INSTANCE: Injector

        val expenseViewModel: ExpenseViewModel
            get() = INSTANCE.expenseViewModel

        val menuCallbacks: MenuCallbacks
            get() = INSTANCE.menuCallbacks

        fun init(
            databaseDriverFactory: DatabaseDriverFactory,
            coroutineScope: CoroutineScope
        ) {
            INSTANCE = Injector(databaseDriverFactory, coroutineScope)
            menuCallbacks.onLoadXlsx = {
                expenseViewModel.showXlsxChooserDialog(show = true, isSave = false)
            }
            menuCallbacks.onSaveXlsx = {
                expenseViewModel.showXlsxChooserDialog(show = true, isSave = true)
            }
            menuCallbacks.onShowStatistics = {
                expenseViewModel.showStatistics()
            }
        }
    }
}