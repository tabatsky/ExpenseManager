package jatx.expense.manager.di

import com.squareup.sqldelight.db.SqlDriver
import jatx.expense.manager.data.cbr.CurrencyRatesGetterImpl
import jatx.expense.manager.data.db.DatabaseDriverFactory
import jatx.expense.manager.data.repository.PaymentRepositoryImpl
import jatx.expense.manager.data.xlsx.XlsxParserFactoryImpl
import jatx.expense.manager.data.xlsx.XlsxSaverFactoryImpl
import jatx.expense.manager.db.AppDatabase
import jatx.expense.manager.domain.cbr.CurrencyRatesGetter
import jatx.expense.manager.domain.repository.PaymentRepository
import jatx.expense.manager.domain.xlsx.XlsxParserFactory
import jatx.expense.manager.domain.xlsx.XlsxSaverFactory
import jatx.expense.manager.presentation.menu.MenuCallbacks
import jatx.expense.manager.presentation.viewmodel.ExpenseViewModel
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import kotlin.properties.Delegates

var appComponent by Delegates.notNull<AppComponent>()

@Component
abstract class AppComponent(
    @get: Provides protected val coroutineScope: CoroutineScope
) {
    private val driverFactory: DatabaseDriverFactory = DatabaseDriverFactory()

    private val driver: SqlDriver = driverFactory.createDriver()

    private val appDatabase: AppDatabase = AppDatabase.invoke(driver)

    abstract val _expenseViewModel: ExpenseViewModel
    val expenseViewModel = _expenseViewModel

    val menuCallbacks = MenuCallbacks().apply {
        onLoadXlsx = {
            expenseViewModel.showXlsxChooserDialog(show = true, isSave = false)
        }
        onSaveXlsx = {
            expenseViewModel.showXlsxChooserDialog(show = true, isSave = true)
        }
        onShowStatisticsByComment = {
            expenseViewModel.showStatisticsByComment()
        }
        onShowStatisticsByCategory = {
            expenseViewModel.showStatisticsByCategory()
        }
        onShowPieChart = {
            expenseViewModel.showPieChart()
        }
        onShowPieChartByComment = {
            expenseViewModel.showPieChartByComment()
        }
        onShowByMonthChart = {
            expenseViewModel.showByMonthChart()
        }
    }

    //@Provides
    //protected fun driverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()

    @Provides
    protected fun provideAppDatabase(): AppDatabase = appDatabase

    protected val PaymentRepositoryImpl.bind: PaymentRepository
        @Provides get() = this

    protected val XlsxParserFactoryImpl.bind: XlsxParserFactory
        @Provides get() = this

    protected val XlsxSaverFactoryImpl.bind: XlsxSaverFactory
        @Provides get() = this

    protected val CurrencyRatesGetterImpl.bind: CurrencyRatesGetter
        @Provides get() = this
}