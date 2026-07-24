package jatx.expense.manager.data.firebase

import com.google.gson.Gson
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.app
import dev.gitlive.firebase.firestore.firestore
import jatx.expense.manager.data.backup.BackupData
import jatx.expense.manager.data.backup.BackupTimeKeeper
import jatx.expense.manager.data.backup.MonthlyBackupData
import jatx.expense.manager.data.backup.toPaymentEntry
import jatx.expense.manager.data.backup.toPaymentEntryGson
import jatx.expense.manager.data.skipset.expenseCommentSetKey
import jatx.expense.manager.data.skipset.incomingCommentSetKey
import jatx.expense.manager.data.skipset.incomingSetKey
import jatx.expense.manager.data.skipset.readSetLines
import jatx.expense.manager.data.skipset.reduceSetKey
import jatx.expense.manager.data.skipset.skipCommentSetKey
import jatx.expense.manager.data.skipset.skipSetKey
import jatx.expense.manager.data.skipset.totalSkipSetKey
import jatx.expense.manager.data.skipset.writeSetLines
import jatx.expense.manager.domain.models.CellKey
import jatx.expense.manager.domain.models.ExpenseEntry
import jatx.expense.manager.domain.models.ExpenseTable
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.models.RowKey
import jatx.expense.manager.domain.util.dateFromMonthKey
import jatx.expense.manager.domain.util.dateOfMonthLastDayFromMonthKey
import jatx.expense.manager.domain.util.formattedMonthAndYear
import jatx.expense.manager.domain.util.monthKey
import jatx.expense.manager.platform.isAndroid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.let

private val db by lazy {
    Firebase.firestore(Firebase.app("ExpenseManager"))
}

suspend fun loadDataFromFirestore(backupTimeKeeper: BackupTimeKeeper) = theUser?.let { user ->
    withContext(Dispatchers.IO) {
        val userUid = user.uid

        try {
            val backupDataStr = db.collection("backups")
                .document(userUid)
                .get()
                .get<String>("backupDataStr")
            val backupData = Gson().fromJson(backupDataStr, BackupData::class.java)
            println("get data from firestore success")

            println(Date(backupTimeKeeper.lastSyncTime))
            println(Date(backupData.lastSyncTime))

            if (isAndroid) {
                writeSetLines(expenseCommentSetKey, backupData.expenseCommentSet ?: listOf())
                writeSetLines(incomingCommentSetKey, backupData.incomingCommentSet ?: listOf())
                writeSetLines(incomingSetKey, backupData.incomingSet ?: listOf())
                writeSetLines(reduceSetKey, backupData.reduceSet ?: listOf())
                writeSetLines(skipCommentSetKey, backupData.skipCommentSet ?: listOf())
                writeSetLines(skipSetKey, backupData.skipSet ?: listOf())
                writeSetLines(totalSkipSetKey, backupData.totalSkipSet ?: listOf())
            }

            val payments = arrayListOf<PaymentEntry>()

            if (backupData.lastSyncTime > backupTimeKeeper.lastSyncTime) {
                backupData.monthKeys.forEach { monthKey ->
                    val monthlyBackupDataStr = db.collection("backups")
                        .document("${userUid}_${monthKey}")
                        .get()
                        .get<String>("monthlyBackupDataStr")
                    val monthlyBackupData = Gson().fromJson(monthlyBackupDataStr, MonthlyBackupData::class.java)
                    println("get monthly data ${monthKey.dateFromMonthKey.formattedMonthAndYear} from firestore success")
                    val monthlyPayments = monthlyBackupData.payments.map { it.toPaymentEntry() }
                    payments.addAll(monthlyPayments)
                }

                val deltaSeconds =
                    (backupData.lastSyncTime - backupTimeKeeper.lastSyncTime) * 0.001f

                println("firestore data was updated: ${payments.size} payments, $deltaSeconds seconds ago")

                val dates = payments
                    .map { it.date.monthKey }
                    .distinct()
                    .map { it.dateOfMonthLastDayFromMonthKey }
                    .sorted()
                val rowKeys = payments
                    .distinctBy { it.rowKeyInt }
                    .map { RowKey(it.cardName, it.category, it.rowKeyInt) }
                    .sortedBy { it.rowKeyInt }

                val allCells = hashMapOf<CellKey, ExpenseEntry>()

                dates.forEach { date ->
                    rowKeys.forEach { rowKey ->
                        val cellPayments = payments
                            .filter {
                                it.date.monthKey == date.monthKey &&
                                        it.rowKeyInt == rowKey.rowKeyInt
                            }
                            .sortedBy {
                                it.date
                            }
                        val expenseEntry = ExpenseEntry(
                            rowKey.cardName,
                            rowKey.category,
                            rowKey.rowKeyInt,
                            date,
                            cellPayments
                        )
                        allCells[CellKey(rowKey.cardName, rowKey.category, date.monthKey)] =
                            expenseEntry
                    }
                }

                val expenseTable = let {
                    val allCellsWithoutPaymentId = allCells
                        .map { entry ->
                            val paymentsWithoutId = entry.value.payments.map {
                                it.copy(id = 0)
                            }
                            val valueWithoutPaymentId = entry.value.copy(_payments = paymentsWithoutId)
                            entry.key to valueWithoutPaymentId
                        }
                        .toMap()
                    ExpenseTable(allCellsWithoutPaymentId, dates, rowKeys)
                }

                println("save data to db success")

                backupTimeKeeper.lastSyncTime = backupData.lastSyncTime

                expenseTable
            } else {
                println("firestore data was not updated")
                null
            }
        } catch (t: Throwable) {
            t.printStackTrace()

            null
        }
    }
}

suspend fun saveDataToFirestore(localData: List<PaymentEntry>, backupTimeKeeper: BackupTimeKeeper) {
    if (backupTimeKeeper.lastChangeTime <= backupTimeKeeper.lastSyncTime) {
        println("no changes, backup skipping")
        return
    }

    theUser?.let { user ->
        withContext(Dispatchers.IO) {
            val currentTime = System.currentTimeMillis()

            val monthKeys = localData
                .map { it.date.monthKey }
                .distinct()
                .sorted()
            val backupData = BackupData(
                monthKeys = monthKeys,
                lastSyncTime = currentTime,
                expenseCommentSet = readSetLines(expenseCommentSetKey),
                incomingCommentSet = readSetLines(incomingCommentSetKey),
                incomingSet = readSetLines(incomingSetKey),
                reduceSet = readSetLines(reduceSetKey),
                skipCommentSet = readSetLines(skipCommentSetKey),
                skipSet = readSetLines(skipSetKey),
                totalSkipSet = readSetLines(totalSkipSetKey)
            )
            val backupDataStr = Gson().toJson(backupData)
            val userUid = user.uid

            val doc = hashMapOf(
                "backupDataStr" to backupDataStr
            )

            try {
                db.collection("backups")
                    .document(userUid)
                    .set(doc)
                backupTimeKeeper.lastSyncTime = currentTime
                println(Date(currentTime))
                println("backup success")
            } catch (t: Throwable) {
                t.printStackTrace()
            }

            for (monthKey in monthKeys) {
                val data = localData
                    .filter { it.date.monthKey == monthKey }
                    .map { it.toPaymentEntryGson() }
                val monthlyBackupData = MonthlyBackupData(
                    monthKey = monthKey,
                    payments = data
                )
                val monthlyBackupDataStr = Gson().toJson(monthlyBackupData)
                val doc = hashMapOf(
                    "monthlyBackupDataStr" to monthlyBackupDataStr
                )
                try {
                    db.collection("backups")
                        .document("${userUid}_${monthKey}")
                        .set(doc)
                    backupTimeKeeper.lastSyncTime = currentTime
                    println(Date(currentTime))
                    println("monthly backup ${monthKey.dateFromMonthKey.formattedMonthAndYear} success")
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }
    }
}