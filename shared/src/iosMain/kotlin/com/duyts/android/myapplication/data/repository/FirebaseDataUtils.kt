package com.duyts.android.myapplication.data.repository

import dev.gitlive.firebase.storage.Data
import platform.Foundation.NSData
import platform.Foundation.create
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.BetaInteropApi

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun ByteArray.toData(): Data {
    val nsData = this.usePinned { 
        NSData.create(bytes = it.addressOf(0), length = this.size.toULong()) 
    }
    return Data(nsData)
}
