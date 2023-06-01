package jatx.expense.manager.di

import jatx.expense.manager.data.db.DatabaseDriverFactory
import jatx.expense.manager.data.repository.PaymentRepositoryImpl
import jatx.expense.manager.db.AppDatabase
import jatx.expense.manager.domain.repository.PaymentRepository
import jatx.expense.manager.domain.usecase.LoadExpenseTableFromDBUseCase
import jatx.expense.manager.domain.usecase.SaveExpenseTableToDBUseCase
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
    private val saveExpenseTableToDBUseCase =
        SaveExpenseTableToDBUseCase(paymentRepository, coroutineScope)
    private val loadExpenseTableFromDBUseCase =
        LoadExpenseTableFromDBUseCase(paymentRepository, coroutineScope)
    private val expenseViewModel =
        ExpenseViewModel(
            saveExpenseTableToDBUseCase,
            loadExpenseTableFromDBUseCase,
            coroutineScope
        )

    companion object {
        private lateinit var INSTANCE: Injector

        val expenseViewModel: ExpenseViewModel
            get() = INSTANCE.expenseViewModel

        fun init(databaseDriverFactory: DatabaseDriverFactory, coroutineScope: CoroutineScope) {
            INSTANCE = Injector(databaseDriverFactory, coroutineScope)
        }
    }
}