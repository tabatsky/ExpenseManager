package jatx.expense.manager.data.db

import com.squareup.sqldelight.db.SqlDriver

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}