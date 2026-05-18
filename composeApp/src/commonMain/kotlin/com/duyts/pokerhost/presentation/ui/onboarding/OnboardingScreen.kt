package com.duyts.pokerhost.presentation.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pokerhost.composeapp.generated.resources.Res
import pokerhost.composeapp.generated.resources.get_started
import pokerhost.composeapp.generated.resources.img_onboarding_1
import pokerhost.composeapp.generated.resources.img_onboarding_2
import pokerhost.composeapp.generated.resources.img_onboarding_3
import pokerhost.composeapp.generated.resources.next
import pokerhost.composeapp.generated.resources.onboarding_desc_1
import pokerhost.composeapp.generated.resources.onboarding_desc_2
import pokerhost.composeapp.generated.resources.onboarding_desc_3
import pokerhost.composeapp.generated.resources.onboarding_title_1
import pokerhost.composeapp.generated.resources.onboarding_title_2
import pokerhost.composeapp.generated.resources.onboarding_title_3

data class OnboardingPage(
	val title: StringResource,
	val description: StringResource,
	val image: DrawableResource,
)

@Composable
fun OnboardingScreen(
	onFinish: () -> Unit,
) {
	val pages = listOf(
		OnboardingPage(
			title = Res.string.onboarding_title_1,
			description = Res.string.onboarding_desc_1,
			image = Res.drawable.img_onboarding_1
		),
		OnboardingPage(
			title = Res.string.onboarding_title_2,
			description = Res.string.onboarding_desc_2,
			image = Res.drawable.img_onboarding_2
		),
		OnboardingPage(
			title = Res.string.onboarding_title_3,
			description = Res.string.onboarding_desc_3,
			image = Res.drawable.img_onboarding_3
		)
	)

	val pagerState = rememberPagerState(pageCount = { pages.size })
	val scope = rememberCoroutineScope()

	Box(modifier = Modifier.fillMaxSize()) {
		HorizontalPager(
			state = pagerState,
			modifier = Modifier.fillMaxSize()
		) { pageIndex ->
			val page = pages[pageIndex]
			Box(modifier = Modifier.fillMaxSize()) {
				Image(
					painter = painterResource(page.image),
					contentDescription = null,
					modifier = Modifier.fillMaxSize(),
					contentScale = ContentScale.Crop,
				)
				// Dark gradient overlay to ensure text readability
				Box(
					modifier = Modifier
						.fillMaxSize()
						.background(
							brush = Brush.verticalGradient(
								colors = listOf(
									Color.Transparent,
									Color.Black.copy(alpha = 0.3f),
									Color.Black.copy(alpha = 0.9f)
								),
								startY = 300f
							)
						)
				)
			}
		}

		// Content overlay
		Column(
			modifier = Modifier
				.fillMaxSize()
				.safeDrawingPadding()
				.padding(horizontal = 24.dp, vertical = 32.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Bottom
		) {
			Text(
				text = stringResource(pages[pagerState.currentPage].title),
				style = MaterialTheme.typography.headlineMedium,
				fontWeight = FontWeight.Bold,
				textAlign = TextAlign.Center,
				color = Color.White
			)
			Spacer(modifier = Modifier.height(16.dp))
			Text(
				text = stringResource(pages[pagerState.currentPage].description),
				style = MaterialTheme.typography.bodyLarge,
				textAlign = TextAlign.Center,
				color = Color.White.copy(alpha = 0.8f)
			)

			Spacer(modifier = Modifier.height(48.dp))

			Row(
				modifier = Modifier
					.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					repeat(pages.size) { iteration ->
						val color = if (pagerState.currentPage == iteration)
							Color.White
						else
							Color.White.copy(alpha = 0.3f)

						Box(
							modifier = Modifier
								.size(10.dp)
								.clip(CircleShape)
								.background(color)
						)
					}
				}

				Button(
					onClick = {
						if (pagerState.currentPage < pages.size - 1) {
							scope.launch {
								pagerState.animateScrollToPage(pagerState.currentPage + 1)
							}
						} else {
							onFinish()
						}
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = Color.White,
						contentColor = Color.Black
					),
					shape = MaterialTheme.shapes.medium,
					contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
				) {
					Text(
						text = if (pagerState.currentPage == pages.size - 1) stringResource(Res.string.get_started) else stringResource(
							Res.string.next
						),
						fontWeight = FontWeight.Bold
					)
				}
			}
		}
	}
}
