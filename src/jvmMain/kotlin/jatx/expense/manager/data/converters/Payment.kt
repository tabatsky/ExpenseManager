package jatx.expense.manager.data.converters

import jatx.expense.manager.db.PaymentEntity
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.util.asDbPresentation
import jatx.expense.manager.domain.util.fromDbPresentation

fun PaymentEntry.toDBEntity() = PaymentEntity(
    id = id,
    cardName = cardName,
    category = category,
    rowKeyInt = rowKeyInt.toLong(),
    date = date.asDbPresentation,
    amount = amount.toLong(),
    comment = comment
)

fun PaymentEntity.toModelEntry() = PaymentEntry(
    id = id,
    cardName = cardName,
    category = category,
    rowKeyInt = rowKeyInt.toInt(),
    date = date.fromDbPresentation,
    amount = amount.toInt(),
    comment = comment
)