package jatx.expense.manager.di

import jatx.expense.manager.data.cbr.CurrencyRatesGetterImpl
import jatx.expense.manager.data.db.AppDatabase
import jatx.expense.manager.data.db.getDatabaseBuilder
import jatx.expense.manager.data.db.getRoomDatabase
import jatx.expense.manager.data.repository.PaymentRepositoryImpl
import jatx.expense.manager.data.xlsx.XlsxParserFactoryImpl
import jatx.expense.manager.data.xlsx.XlsxSaverFactoryImpl
import jatx.expense.manager.domain.cbr.CurrencyRatesGetter
import jatx.expense.manager.domain.repository.PaymentRepository
import jatx.expense.manager.domain.xlsx.XlsxParserFactory
import jatx.expense.manager.domain.xlsx.XlsxSaverFactory
import jatx.expense.manager.presentation.menu.MenuCallbacks
import jatx.expense.manager.presentation.viewmodel.ExpenseViewModel
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import kotlin.properties.Delegates

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class AppScope

var appComponent by Delegates.notNull<AppComponent>()

@AppScope
@Component
abstract class AppComponent(
    @get: Provides protected val coroutineScope: CoroutineScope
) {
    abstract val expenseViewModel: ExpenseViewModel

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
        onShowPieChartByCommentMinus = {
            expenseViewModel.showPieChartByCommentMinus()
        }
        onShowByMonthChart = {
            expenseViewModel.showByMonthChart()
        }
    }

    @AppScope
    @Provides
    protected fun provideAppDatabase(): AppDatabase = getRoomDatabase(getDatabaseBuilder())

    protected val PaymentRepositoryImpl.bind: PaymentRepository
        @Provides get() = this

    protected val XlsxParserFactoryImpl.bind: XlsxParserFactory
        @Provides get() = this

    protected val XlsxSaverFactoryImpl.bind: XlsxSaverFactory
        @Provides get() = this

    protected val CurrencyRatesGetterImpl.bind: CurrencyRatesGetter
        @Provides get() = this
}