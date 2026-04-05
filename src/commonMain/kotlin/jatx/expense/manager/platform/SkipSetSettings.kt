package jatx.expense.manager.platform

val loadSkipSetsFromFiles = if (isAndroid)
    loadSkipSetsFromFilesAndroid
else
    loadSkipSetsFromFilesJvm

const val loadSkipSetsFromFilesAndroid = false
const val loadSkipSetsFromFilesJvm = true