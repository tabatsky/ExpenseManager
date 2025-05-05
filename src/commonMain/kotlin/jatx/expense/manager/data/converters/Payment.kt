package jatx.expense.manager.data.converters

import jatx.expense.manager.data.db.entity.PaymentEntity
import jatx.expense.manager.domain.models.PaymentEntry
import jatx.expense.manager.domain.util.asDbPresentation
import jatx.expense.manager.domain.util.fromDbPresentation

fun PaymentEntry.toDBEntity() = PaymentEntity(
    id = id.takeIf { it > 0L },
    cardName = cardName,
    category = category,
    rowKeyInt = rowKeyInt,
    date = date.asDbPresentation,
    amount = amount,
    comment = comment,
    currency = currency
)

fun PaymentEntity.toModelEntry() = PaymentEntry(
    id = id ?: 0L,
    cardName = cardName,
    category = category,
    rowKeyInt = rowKeyInt,
    date = date.fromDbPresentation,
    amount = amount,
    comment = comment
)