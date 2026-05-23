package com.duyts.pokerhost.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun BannerAd(modifier: Modifier, adId: String) {
	// Placeholder for Web Ad integration (e.g. AdSense)
	Box(
		modifier = modifier.fillMaxWidth().height(50.dp),
		contentAlignment = Alignment.Center
	) {
		Text("Web Banner Ad Placeholder")
	}
}
