package com.duyts.pokerhost

interface Platform {
	val name: String
	fun getSessionId(): String? = null
	fun getUrlPath(): String = ""
	fun openUrl(url: String) {}
	fun isAndroid(): Boolean = false
	fun isIOS(): Boolean = false
}

expect fun getPlatform(): Platform