package jatx.expense.manager.res

import jatx.expense.manager.domain.util.cp1251toUTF8

val totalCardName = "-".cp1251toUTF8()
val cashCardName = "��������".cp1251toUTF8()
val totalCategory = "�����".cp1251toUTF8()
val totalPlusCategory = "���������".cp1251toUTF8()
val totalPlus2Category = "��������� 2".cp1251toUTF8()
val totalMinusCategory = "��������".cp1251toUTF8()
val totalMinus2Category = "�������� 2".cp1251toUTF8()
val totalWithCashCategory = "����� � ���������".cp1251toUTF8()
val totalDate = "�����".cp1251toUTF8()
val lohCategory = "���������".cp1251toUTF8()
val totalLohCategory = "����� ���������".cp1251toUTF8()

val usdCategory = "�������".cp1251toUTF8()
val cnyCategory = "����".cp1251toUTF8()
val investCategory = "����������".cp1251toUTF8()
val incomingCategory = "�����������".cp1251toUTF8()

val salaryComment = "���������".cp1251toUTF8()

val String.currencyForCategory: String
    get() = when (this) {
        usdCategory -> "USD"
        cnyCategory -> "CNY"
        else -> "RUR"
    }

const val defaultCommentPositiveAmount = "������"
const val defaultCommentNegativeAmount = "�����"
const val defaultCommentZeroAmount = "---"

const val buttonSaveLabel = "���������"
const val buttonSaveZeroLabel = "������� �����, �������� �� ����"
const val buttonAddLabel = "��������"
const val buttonDeleteLabel = "�������"
const val buttonYesLabel = "��"
const val buttonNoLabel = "���"
const val buttonChooseLabel = "�������"
const val buttonCancelLabel = "������"

const val titleDeletionConfirmationDialog = "������ ����� �������"
const val textDeletionConfirmationDialog = "�� �������?"

const val menuFileLabel = "����"
const val menuLoadXlsxLabel = "��������� XLSX"
const val menuSaveXlsxLabel = "��������� XLSX"
const val menuFunctionLabel = "�������"
const val menuStatisticsByCommentLabel = "���������� �� �����"
const val menuStatisticsByCategoryLabel = "���������� �� ����������"
const val menuPieChart = "��������� ��������"
const val menuByMonthChart = "������ �� �������"

const val dialogFileChooserOpenTitle = "��������� XLSX"
const val dialogFileChooserSaveTitle = "��������� XLSX"
const val dialogFileChooserXLSXDescription = "������� Excel"
const val dialogFileChooserXLSXExtension = "xlsx"

const val msgWrongNumberFormat = "Wrong number format"

const val labelOverallTime = "�� �� �����"
const val labelOverallCategory = "�����"

const val labelShowSkipped = "���������� �����������"