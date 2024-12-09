package jatx.expense.manager.data.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import jatx.expense.manager.db.AppDatabase
import java.io.File

const val dbFile = "ExpenseManager.db"

class DatabaseDriverFactory {
    fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbFile")
        if (!File(dbFile).exists()) {
            AppDatabase.Schema.create(driver)
            AppDatabase
                .invoke(driver)
                .paymentEntityQueries
                .createTableIfNotExists()
        } else {
            val oldVersion = driver
                .executeQuery(
                    sql = "PRAGMA user_version",
                    identifier = null,
                    parameters = 0)
                .getLong(0)
                ?.toInt() ?: 0

            val newVersion = AppDatabase.Schema.version
            println("old version: $oldVersion; new version: $newVersion")
            try {
                AppDatabase.Schema.migrate(
                    driver = driver,
                    oldVersion = oldVersion,
                    newVersion = newVersion
                )
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            driver
                .execute(
                    sql = "PRAGMA user_version = $newVersion",
                    identifier = null,
                    parameters = 0)
        }
        return driver
    }
}