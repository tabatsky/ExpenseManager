package jatx.expense.manager.platform

val loadSkipSets = if (isAndroid)
    loadSkipSetsAndroid
else
    loadSkipSetsJvm

const val loadSkipSetsAndroid = true
const val loadSkipSetsJvm = true