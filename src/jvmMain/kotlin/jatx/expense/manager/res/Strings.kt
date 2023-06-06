package jatx.expense.manager.res

import jatx.expense.manager.domain.util.cp1251toUTF8


val totalCardName = "-".cp1251toUTF8()
val totalCategory = "�����".cp1251toUTF8()
val totalDate = "�����".cp1251toUTF8()
val lohCategory = "���������".cp1251toUTF8()

const val defaultCommentPositiveAmount = "������"
const val defaultCommentNegativeAmount = "�����"
const val defaultCommentZeroAmount = "---"

const val buttonSaveLabel = "���������"
const val buttonAddLabel = "��������"

const val menuFileLabel = "����"
const val menuLoadXlsxLabel = "��������� XLSX"

const val dialogFileChooserTitle = "������� XLSX"
const val dialogFileChooserXLSXDescription = "������� Excel"
const val dialogFileChooserXLSXExtension = "xlsx"

const val msgWrongNumberFormat = "Wrong number format"