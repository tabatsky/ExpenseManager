package jatx.expense.manager.domain.models

import jatx.expense.manager.res.*

const val incomingKey = 700
const val cnyKey = 800
const val usdKey = 810
const val investKey = 890
const val lohKey = 900

data class RowKey(
    val cardName: String,
    val category: String,
    val rowKeyInt: Int = 0
) {
    val label = "$cardName|$category"

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
                val categoryKey = when (category) {
                    lohCategory -> lohKey
                    usdCategory -> usdKey
                    cnyCategory -> cnyKey
                    investCategory -> investKey
                    incomingCategory -> incomingKey
                    else -> (categories
                        .indexOf(category)
                        .takeIf { it >= 0 }
                        ?: run {
                            categories.add(category)
                            categories.size - 1
                        }) + 1
                }

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

fun makeTotalPlusRowKey(cardNameKey: Int) =
    1000 * cardNameKey + 400

fun makeTotalPlus2RowKey(cardNameKey: Int) =
    1000 * cardNameKey + 500

fun makeTotalMinusRowKey(cardNameKey: Int) =
    1000 * cardNameKey + 600