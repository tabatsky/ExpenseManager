package jatx.expense.manager.domain.models

import jatx.expense.manager.res.lohCategory
import jatx.expense.manager.res.totalCardName
import jatx.expense.manager.res.totalCategory

const val lohKey = 900

data class RowKey(
    val cardName: String,
    val category: String,
    val rowKeyInt: Int = 0
) {
    fun withIntKey(allCardNames: ArrayList<String>, allCategories: ArrayList<ArrayList<String>>): RowKey {
        val key = if (cardName == totalCardName && category == totalCategory) {
            0
        } else {
            val cardNameKey = (allCardNames
                .indexOf(cardName)
                .takeIf { it >= 0 }
                ?: run {
                    allCardNames.add(cardName)
                    allCategories.add(arrayListOf())
                    allCardNames.size - 1
                }) + 1

            if (category == totalCategory) {
                makeTotalRowKey(cardNameKey)
            } else run {
                val categories = allCategories[cardNameKey - 1]
                val categoryKey = (if (category == lohCategory)
                    lohKey
                else
                    categories
                        .indexOf(category)
                        .takeIf { it >= 0 }
                        ?: run {
                            categories.add(category)
                            categories.size - 1
                        }) + 1
                makeRowKey(cardNameKey, categoryKey)
            }
        }

        return copy(cardName=cardName, category=category, rowKeyInt=key)
    }
}

val Int.cardNameKey: Int
    get() = this / 1000

val Int.categoryKey: Int
    get() = this % 1000

fun makeRowKey(cardNameKey: Int, categoryKey: Int) =
    1000 * cardNameKey + categoryKey

fun makeTotalRowKey(cardNameKey: Int) =
    1000 * cardNameKey