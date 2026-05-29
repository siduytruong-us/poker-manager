package com.duyts.pokerhost.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

object DateTimeUtils {
	fun formatCurrentTimeHHmm(): String {
		val now = ClockUtils.nowLocal()
		val hour = now.hour.toString().padStart(2, '0')
		val minute = now.minute.toString().padStart(2, '0')
		return "$hour:$minute"
	}

	fun formatDate(timestamp: Long): String {
		val instant = ClockUtils.fromEpochMilliseconds(timestamp)
		val dateTime = ClockUtils.toLocalDateTime(instant)
		val today = ClockUtils.nowLocal().date

		return when (dateTime.date) {
			today -> "Today"
			today.minus(1, DateTimeUnit.DAY) -> "Yesterday"
			else -> "${dateTime.date.day}/${dateTime.date.month.ordinal + 1}/${dateTime.year}"
		}
	}
}
