package com.duyts.pokerhost.util

import kotlin.math.abs

object CurrencyUtils {
	/**
	 * Chuyển đổi từ cent (Int) sang Dollar (Float).
	 */
	fun centsToDollars(cents: Int): Float {
		return cents / 100f
	}

	/**
	 * Chuyển đổi từ Dollar (Float) sang cent (Int).
	 */
	fun dollarsToCents(dollars: Float): Int {
		return (dollars * 100).toInt()
	}

	/**
	 * Định dạng số tiền để hiển thị (ví dụ: $5.0, -$10.5).
	 */
	fun format(amount: Float): String {
		val prefix = if (amount >= 0) "$" else "-$"
		val absoluteValue = abs(amount)

		// Kiểm tra xem có phần thập phân không để hiển thị đẹp hơn
		return if (absoluteValue % 1 == 0f) {
			"$prefix${absoluteValue.toInt()}"
		} else {
			"$prefix$absoluteValue"
		}
	}
}
