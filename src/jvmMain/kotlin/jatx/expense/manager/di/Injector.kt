package jatx.expense.manager.di

import jatx.expense.manager.data.db.DatabaseDriverFactory
import jatx.expense.manager.data.repository.PaymentRepositoryImpl
import jatx.expense.manager.data.xlsx.XlsxParserFactoryImpl
import jatx.expense.manager.db.AppDatabase
import jatx.expense.manager.domain.repository.PaymentRepository
import jatx.expense.manager.domain.usecase.LoadExpenseTableFromDBUseCase
import jatx.expense.manager.domain.usecase.LoadXlsxUseCase
import jatx.expense.manager.domain.usecase.SaveExpenseTableToDBUseCase
import jatx.expense.manager.domain.usecase.UpdatePaymentUseCase
import jatx.expense.manager.domain.xlsx.XlsxParserFactory
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
    private val saveExpenseTableToDBUseCase =
        SaveExpenseTableToDBUseCase(paymentRepository)
    private val loadExpenseTableFromDBUseCase =
        LoadExpenseTableFromDBUseCase(paymentRepository)
    private val loadXlsxUseCase =
        LoadXlsxUseCase(xlsxParserFactory)
    private val updatePaymentUseCase =
        UpdatePaymentUseCase(paymentRepository)
    private val expenseViewModel =
        ExpenseViewModel(
            saveExpenseTableToDBUseCase,
            loadExpenseTableFromDBUseCase,
            loadXlsxUseCase,
            updatePaymentUseCase,
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
                expenseViewModel.showXlsxChooserDialog(true)
            }
        }
    }
}