package com.duyts.pokerhost.presentation.ui.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import pokerhost.composeapp.generated.resources.Res
import pokerhost.composeapp.generated.resources.img_onboarding_1

@Composable
fun LandingScreen(
	sessionId: String?,
	onOpenApp: () -> Unit,
) {
	Surface(modifier = Modifier.fillMaxSize()) {
		Box(modifier = Modifier.fillMaxSize()) {
			Image(
				painter = painterResource(Res.drawable.img_onboarding_1),
				contentDescription = null,
				modifier = Modifier.fillMaxSize(),
				contentScale = ContentScale.Crop
			)

			// Semi-transparent overlay to make text readable
			Surface(
				modifier = Modifier.fillMaxSize(),
				color = Color.Black.copy(alpha = 0.5f)
			) {}

			Column(
				modifier = Modifier
					.fillMaxWidth()
					.fillMaxHeight()
					.padding(24.dp)
					.padding(top = 64.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Text(
					text = "PokerHost",
					style = MaterialTheme.typography.headlineLarge,
					fontWeight = FontWeight.Black,
					color = Color.White,
				)

				Spacer(Modifier.height(48.dp))

				Text(
					text = "You're Invited!",
					style = MaterialTheme.typography.displaySmall,
					fontWeight = FontWeight.Bold,
					textAlign = TextAlign.Center,
					color = Color.White
				)

				Spacer(Modifier.height(16.dp))

				Text(
					text = "Join the poker session and start tracking your game in real-time.",
					style = MaterialTheme.typography.bodyLarge,
					textAlign = TextAlign.Center,
					color = Color.White.copy(alpha = 0.8f)
				)

				Spacer(Modifier.weight(1f))

				if (sessionId != null) {
					Button(
						onClick = onOpenApp,
						modifier = Modifier
							.fillMaxWidth()
							.height(56.dp),
						shape = MaterialTheme.shapes.medium
					) {
						Text("Open in PokerHost App", fontSize = 18.sp)
					}
				}

				Spacer(Modifier.height(32.dp))
			}
		}
	}
}

@Composable
@Preview
fun LandingScreenPreview() {
	LandingScreen(
		sessionId = "test-session-id",
		onOpenApp = {}
	)
}
