package jatx.expense.manager.domain.usecase

import jatx.expense.manager.di.AppScope
import jatx.expense.manager.domain.repository.PaymentRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SwapRowKeysIntUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend fun execute(rowKeyInt1: Int, rowKeyInt2: Int) =
        paymentRepository.swapRowKeysInt(rowKeyInt1, rowKeyInt2)
}