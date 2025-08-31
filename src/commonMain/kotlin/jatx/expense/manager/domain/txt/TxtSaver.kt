import jatx.expense.manager.domain.models.PaymentEntry
import java.util.Date

interface TxtSaver {
    fun savePayments(payments: List<PaymentEntry>, cardName: String, category: String, date: Date)
}
