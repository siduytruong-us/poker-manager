package com.duyts.pokerhost.data.repository

import dev.gitlive.firebase.storage.Data
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun ByteArray.toData(): Data {
	val nsData = this.usePinned {
		NSData.create(bytes = it.addressOf(0), length = this.size.toULong())
	}
	return Data(nsData)
}
