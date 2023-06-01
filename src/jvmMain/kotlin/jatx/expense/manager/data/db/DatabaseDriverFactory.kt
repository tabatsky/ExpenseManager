package jatx.expense.manager.data.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import jatx.expense.manager.db.AppDatabase
import java.io.File

const val dbFile = "ExpenseManager.db"

class DatabaseDriverFactory() {
    fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbFile")
        if (!File(dbFile).exists()) {
            AppDatabase.Schema.create(driver)
        }
        AppDatabase
            .invoke(driver)
            .paymentEntityQueries
            .createTableIfNotExists()
        return driver
    }
}