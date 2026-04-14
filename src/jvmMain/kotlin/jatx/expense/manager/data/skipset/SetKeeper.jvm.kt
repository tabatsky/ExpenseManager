package jatx.expense.manager.data.skipset

import java.io.File
import java.nio.charset.Charset

actual fun readSetLines(setKey: String): List<String> =
    File(setKey).readLines(Charset.forName("UTF-8"))

actual fun writeSetLines(setKey: String, lines: List<String>) {
    File(setKey).writeText(
        lines.joinToString(System.lineSeparator()))
}