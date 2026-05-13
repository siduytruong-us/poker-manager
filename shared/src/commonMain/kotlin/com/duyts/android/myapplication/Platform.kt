package com.duyts.android.myapplication

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform