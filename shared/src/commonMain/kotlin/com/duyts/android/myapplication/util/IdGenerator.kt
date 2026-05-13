package com.duyts.android.myapplication.util

object IdGenerator {
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun generate(prefix: String): String {
        val suffix = (1..20)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
        return "$prefix.$suffix"
    }
}
