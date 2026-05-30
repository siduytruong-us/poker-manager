package com.duyts.pokerhost.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object ClockUtils {
	fun now(): Instant =
		Instant.fromEpochMilliseconds(kotlin.time.Clock.System.now().toEpochMilliseconds())

	fun fromEpochMilliseconds(timestamp: Long): Instant =
		Instant.fromEpochMilliseconds(timestamp)

	fun toLocalDateTime(instant: Instant): LocalDateTime =
		instant.toLocalDateTime(TimeZone.currentSystemDefault())

	fun nowLocal(): LocalDateTime = toLocalDateTime(now())
}
