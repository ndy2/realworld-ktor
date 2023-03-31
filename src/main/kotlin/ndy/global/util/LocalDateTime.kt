package ndy.global.util

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

val notUsed = LocalDateTime(1900, 1, 1, 0, 0, 0, 1)