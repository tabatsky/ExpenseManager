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
const val saveToFirestoreOnAppFinishAndroid = true

const val loadFromFirestoreOnAppStartJvm = true
const val saveToFirestoreOnAppFinishJvm = true