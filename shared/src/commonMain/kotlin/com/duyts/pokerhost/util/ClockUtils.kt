package com.duyts.pokerhost.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

object ClockUtils {
	fun now(): Instant = kotlin.time.Clock.System.now()

	fun fromEpochMilliseconds(timestamp: Long): Instant =
		Instant.fromEpochMilliseconds(timestamp)

	fun toLocalDateTime(instant: Instant): LocalDateTime =
		instant.toLocalDateTime(TimeZone.currentSystemDefault())

	fun nowLocal(): LocalDateTime = toLocalDateTime(now())
}
