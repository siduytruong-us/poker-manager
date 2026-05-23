package com.duyts.pokerhost.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

object DateTimeUtils {
	fun formatCurrentTimeHHmm(): String {
		val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
		val hour = now.hour.toString().padStart(2, '0')
		val minute = now.minute.toString().padStart(2, '0')
		return "$hour:$minute"
	}

	fun formatDate(timestamp: Long): String {
		val instant = Instant.fromEpochMilliseconds(timestamp)
		val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
		val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

		return when (dateTime.date) {
			today -> "Today"
			today.minus(1, DateTimeUnit.DAY) -> "Yesterday"
			else -> "${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year}"
		}
	}
}
