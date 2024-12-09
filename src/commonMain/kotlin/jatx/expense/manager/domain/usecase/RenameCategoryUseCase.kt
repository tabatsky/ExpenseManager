package jatx.expense.manager.domain.usecase

import jatx.expense.manager.di.AppScope
import jatx.expense.manager.domain.models.RowKey
import jatx.expense.manager.domain.repository.PaymentRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class RenameCategoryUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend fun execute(newCategory: String, rowKey: RowKey) =
        paymentRepository.renameCategory(newCategory, rowKey.cardName, rowKey.category)
}