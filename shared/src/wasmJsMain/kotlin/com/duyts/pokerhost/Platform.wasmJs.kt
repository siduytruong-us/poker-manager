package com.duyts.pokerhost

class WasmPlatform : Platform {
	override val name: String = "Web with Kotlin/Wasm"
	override fun getSessionId(): String? {
		val search = getSearch()
		if (search.contains("sessionId=")) {
			return search.substringAfter("sessionId=").substringBefore("&")
		}
		return null
	}

	override fun openUrl(url: String) {
		openBrowserUrl(url)
	}

	override fun isAndroid(): Boolean = getUserAgent().contains("Android", ignoreCase = true)
	override fun isIOS(): Boolean =
		listOf("iPhone", "iPad", "iPod").any { getUserAgent().contains(it, ignoreCase = true) }
}

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@JsFun("() => window.navigator.userAgent")
external fun getUserAgent(): String

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@JsFun("() => window.location.search")
external fun getSearch(): String

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@JsFun("(url) => window.location.href = url")
external fun openBrowserUrl(url: String)

actual fun getPlatform(): Platform = WasmPlatform()
