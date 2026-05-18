package com.duyts.pokerhost.util

fun Int?.orZero(): Int = this ?: 0
fun Float?.orZero(): Float = this ?: 0f
fun Double?.orZero(): Double = this ?: 0.0

fun String.toFloatOrZero(): Float = this.toFloatOrNull().orZero()
fun String.toIntOrZero(): Int = this.toIntOrNull().orZero()
fun String.toDoubleOrZero(): Double = this.toDoubleOrNull().orZero()
