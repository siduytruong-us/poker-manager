package com.duyts.pokerhost

class JsPlatform : Platform {
	override val name: String = "Web with Kotlin/JS"
	override fun getSessionId(): String? {
		val search: String = js("window.location.search") as String
		if (search.contains("sessionId=")) {
			return search.substringAfter("sessionId=").substringBefore("&")
		}
		return null
	}

	override fun openUrl(url: String) {
		js("window.location.href = url")
	}

	override fun isAndroid(): Boolean {
		val userAgent = js("window.navigator.userAgent") as String
		return userAgent.contains("Android", ignoreCase = true)
	}

	override fun isIOS(): Boolean {
		val userAgent = js("window.navigator.userAgent") as String
		return listOf("iPhone", "iPad", "iPod").any { userAgent.contains(it, ignoreCase = true) }
	}
}

actual fun getPlatform(): Platform = JsPlatform()
