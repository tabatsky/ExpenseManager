package jatx.expense.manager.platform

val loadFromFirestoreOnAppStart = if (isAndroid)
    loadFromFirestoreOnAppStartAndroid
else
    loadFromFirestoreOnAppStartJvm

val saveToFirestoreOnAppFinish = if (isAndroid)
    saveToFirestoreOnAppFinishAndroid
else
    saveToFirestoreOnAppFinishJvm

const val loadFromFirestoreOnAppStartAndroid = true
const val saveToFirestoreOnAppFinishAndroid = false

const val loadFromFirestoreOnAppStartJvm = false
const val saveToFirestoreOnAppFinishJvm = true