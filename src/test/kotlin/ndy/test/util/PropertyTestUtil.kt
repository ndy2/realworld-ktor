package ndy.test.util

import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.*
import kotlin.random.nextInt

fun RandomSource.int(range: IntRange): Int {
    return this.random.nextInt(range)
}

fun RandomSource.alphaNumericString(size: Int): String {
    return Codepoint.alphanumeric().take(size, this).joinToString("") { it.asString() }
}

fun RandomSource.alphaNumericString(sizeRange: IntRange): String {
    val size = int(sizeRange)
    return Codepoint.alphanumeric().take(size, this).joinToString("") { it.asString() }
}

fun RandomSource.ascii(sizeRange: IntRange): String {
    val size = int(sizeRange)
    return Codepoint.ascii().take(size, this).joinToString("") { it.asString() }
}