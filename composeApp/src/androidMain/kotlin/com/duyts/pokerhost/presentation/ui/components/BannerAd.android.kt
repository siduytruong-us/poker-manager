package com.duyts.pokerhost.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
actual fun BannerAd(modifier: Modifier, adId: String) {
	AndroidView(
		modifier = modifier.fillMaxWidth(),
		factory = { context ->
			AdView(context).apply {
				setAdSize(AdSize.BANNER)
				adUnitId = adId
				loadAd(AdRequest.Builder().build())
			}
		}
	)
}
