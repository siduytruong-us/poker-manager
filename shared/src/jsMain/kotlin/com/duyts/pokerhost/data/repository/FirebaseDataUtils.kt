package com.duyts.pokerhost.data.repository

import dev.gitlive.firebase.storage.Data
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.set

actual fun ByteArray.toData(): Data {
	val uint8Array = Uint8Array(this.size)
	for (i in this.indices) {
		uint8Array[i] = this[i]
	}
	return Data(uint8Array)
}
