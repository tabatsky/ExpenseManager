package jatx.expense.manager.data.backup

expect val loadFromFirestoreOnAppStart: Boolean
expect val saveToFirestoreOnAppFinish: Boolean

//val loadFromFirestoreOnAppStart = true
//val saveToFirestoreOnAppFinish = true