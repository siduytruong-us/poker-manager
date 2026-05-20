package com.duyts.pokerhost.util

import me.tatarka.inject.annotations.Inject

@Inject
class WebShareManager : ShareManager {
	override fun shareSession(sessionId: String, sessionTitle: String) {
		// For now, we can just log or implement a simple share mechanism for web if possible.
		// In a real scenario, we might use window.navigator.share if available.
		println("Sharing session $sessionId: $sessionTitle")
	}
}
