package com.duyts.pokerhost

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.duyts.pokerhost.presentation.theme.AppTheme
import com.duyts.pokerhost.presentation.ui.landing.LandingScreen
import com.duyts.pokerhost.presentation.ui.public.PublicInfoScreen
import com.duyts.pokerhost.util.PublicContent

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
	val platform = getPlatform()
	val sessionId = platform.getSessionId()
	val path = platform.getUrlPath()

	ComposeViewport {
		when {
			path == "/privacy" || path == "/privacy/" -> {
				AppTheme {
					PublicInfoScreen(
						title = "Privacy Policy",
						content = PublicContent.PRIVACY_POLICY,
						onBack = { platform.openUrl("/") }
					)
				}
			}

			path == "/terms" || path == "/terms/" -> {
				AppTheme {
					PublicInfoScreen(
						title = "Terms & Conditions",
						content = PublicContent.TERMS_CONDITIONS,
						onBack = { platform.openUrl("/") }
					)
				}
			}

			sessionId != null -> {
				AppTheme {
					LandingScreen(
						sessionId = sessionId,
						onOpenApp = {
							val deepLink = "pokerhost://join?sessionId=$sessionId"
							val playStoreUrl =
								"https://play.google.com/store/apps/details?id=com.duyts.pokerhost"
							val appStoreUrl = "https://apps.apple.com/app/pokerhost/idXXXXXXXX"

							println("Debug: Button clicked for sessionId: $sessionId")

							when {
								platform.isAndroid() -> {
									println("Debug: Detected Android. Opening deep link: $deepLink")
									platform.openUrl(deepLink)
								}

								platform.isIOS() -> {
									println("Debug: Detected iOS. Opening deep link: $deepLink")
									platform.openUrl(deepLink)
								}

								else -> {
									println("Debug: Detected Desktop/Other. Opening Play Store: $playStoreUrl")
									platform.openUrl(playStoreUrl)
								}
							}
						}
					)
				}
			}

			else -> {
				App()
			}
		}
	}
}
