package com.duyts.pokerhost.util

import android.content.Intent
import com.duyts.pokerhost.di.TodoContext
import me.tatarka.inject.annotations.Inject

@Inject
class AndroidShareManager : ShareManager {
	override fun shareSession(sessionId: String, sessionTitle: String) {
		val context = TodoContext.context

		// This URL should ideally be a landing page that redirects to stores if the app is not installed,
		// or uses App Links / Universal Links to open the app directly.
		val shareLink = "https://poker-host-550ca.web.app/?sessionId=$sessionId"

		val shareText = "Join my poker session '$sessionTitle' on PokerHost!\n\n$shareLink"

		val sendIntent: Intent = Intent().apply {
			action = Intent.ACTION_SEND
			putExtra(Intent.EXTRA_TEXT, shareText)
			type = "text/plain"
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		}

		val shareIntent = Intent.createChooser(sendIntent, null).apply {
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		}
		context.startActivity(shareIntent)
	}
}
