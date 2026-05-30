package com.duyts.pokerhost.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

private const val BANNER_AD_UNIT_ID = "ca-app-pub-2953073229838997/4239209047"

@Composable
actual fun BannerAd(modifier: Modifier, adId: String) {
	AndroidView(
		modifier = modifier.fillMaxWidth(),
		factory = { context ->
			AdView(context).apply {
				setAdSize(AdSize.BANNER)
				adUnitId = BANNER_AD_UNIT_ID
				loadAd(AdRequest.Builder().build())
			}
		}
	)
}
