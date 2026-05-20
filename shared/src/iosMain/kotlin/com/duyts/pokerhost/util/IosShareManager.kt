package com.duyts.pokerhost.util

import me.tatarka.inject.annotations.Inject
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Inject
class IosShareManager : ShareManager {
	override fun shareSession(sessionId: String, sessionTitle: String) {
		val shareLink = "https://poker-host-550ca.web.app/?sessionId=$sessionId"
		val shareText = "Join my poker session '$sessionTitle' on PokerHost!\n\n$shareLink"

		val items = listOf(shareText)
		val activityController = UIActivityViewController(items, null)

		val window = UIApplication.sharedApplication.keyWindow
		window?.rootViewController?.presentViewController(
			activityController,
			animated = true,
			completion = null
		)
	}
}
