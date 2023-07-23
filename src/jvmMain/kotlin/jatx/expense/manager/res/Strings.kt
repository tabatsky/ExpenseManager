package jatx.expense.manager.res

import jatx.expense.manager.domain.util.cp1251toUTF8


val totalCardName = "-".cp1251toUTF8()
val cashCardName = "Наличные".cp1251toUTF8()
val totalCategory = "Всего".cp1251toUTF8()
val totalWithCashCategory = "Всего с наличными".cp1251toUTF8()
val totalDate = "Всего".cp1251toUTF8()
val lohCategory = "Лоханулся".cp1251toUTF8()
val totalLohCategory = "Всего лоханулся".cp1251toUTF8()

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

const val dialogFileChooserOpenTitle = "Загрузить XLSX"
const val dialogFileChooserSaveTitle = "Сохранить XLSX"
const val dialogFileChooserXLSXDescription = "Таблица Excel"
const val dialogFileChooserXLSXExtension = "xlsx"

const val msgWrongNumberFormat = "Wrong number format"