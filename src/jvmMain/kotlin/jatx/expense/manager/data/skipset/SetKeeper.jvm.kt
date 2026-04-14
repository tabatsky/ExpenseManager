package jatx.expense.manager.data.skipset

import java.io.File
import java.nio.charset.Charset

actual fun readSetLines(setKey: String): List<String> =
    File(setKey).readLines(Charset.forName("UTF-8"))