package jatx.expense.manager.platform

val saveToDefaultXlsx = if (isAndroid)
    saveToDefaultXlsxAndroid
else
    saveToDefaultXlsxJvm

const val saveToDefaultXlsxAndroid = false
const val saveToDefaultXlsxJvm = true