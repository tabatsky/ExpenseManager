package jatx.expense.manager.res

import jatx.expense.manager.domain.util.cp1251toUTF8

val totalCardName = "-".cp1251toUTF8()
val cashCardName = "Наличные".cp1251toUTF8()
val totalCategory = "Всего".cp1251toUTF8()
val totalPlusCategory = "Потрачено".cp1251toUTF8()
val totalPlus2Category = "Потрачено 2".cp1251toUTF8()
val totalMinusCategory = "Получено".cp1251toUTF8()
val totalMinus2Category = "Получено 2".cp1251toUTF8()
val totalWithCashCategory = "Всего с наличными".cp1251toUTF8()
val totalDate = "Всего".cp1251toUTF8()
val lohCategory = "Лоханулся".cp1251toUTF8()
val totalLohCategory = "Всего лоханулся".cp1251toUTF8()

val usdCategory = "Доллары".cp1251toUTF8()
val cnyCategory = "Юани".cp1251toUTF8()
val investCategory = "инвестиции".cp1251toUTF8()
val incomingCategory = "Поступления".cp1251toUTF8()

val salaryComment = "Заработал".cp1251toUTF8()

val String.currencyForCategory: String
    get() = when (this) {
        usdCategory -> "USD"
        cnyCategory -> "CNY"
        else -> "RUR"
    }

const val defaultCommentPositiveAmount = "Расход"
const val defaultCommentNegativeAmount = "Доход"
const val defaultCommentZeroAmount = "---"

const val buttonSaveLabel = "Сохранить"
const val buttonSaveZeroLabel = "Введите сумму, отличную от нуля"
const val buttonAddLabel = "Добавить"
const val buttonDeleteLabel = "Удалить"
const val buttonYesLabel = "Да"
const val buttonNoLabel = "Нет"
const val buttonChooseLabel = "Выбрать"
const val buttonCancelLabel = "Отмена"

const val titleDeletionConfirmationDialog = "Запись будет удалена"
const val textDeletionConfirmationDialog = "Вы уверены?"

const val menuFileLabel = "Файл"
const val menuLoadXlsxLabel = "Загрузить XLSX"
const val menuSaveXlsxLabel = "Сохранить XLSX"
const val menuFunctionLabel = "Функция"
const val menuStatisticsByCommentLabel = "Статистика по метке"
const val menuStatisticsByCategoryLabel = "Статистика по категориям"
const val menuPieChart = "Диаграмма расходов"
const val menuByMonthChart = "График по месяцам"

const val dialogFileChooserOpenTitle = "Загрузить XLSX"
const val dialogFileChooserSaveTitle = "Сохранить XLSX"
const val dialogFileChooserXLSXDescription = "Таблица Excel"
const val dialogFileChooserXLSXExtension = "xlsx"

const val msgWrongNumberFormat = "Wrong number format"

const val labelOverallTime = "За всё время"
const val labelOverallCategory = "Всего"

const val labelShowSkipped = "Отображать пропущенные"