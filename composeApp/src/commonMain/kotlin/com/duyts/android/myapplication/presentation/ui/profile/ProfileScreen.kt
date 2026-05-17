package com.duyts.android.myapplication.presentation.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.duyts.android.myapplication.domain.repository.AuthUser
import com.duyts.android.myapplication.presentation.theme.AppTheme
import com.duyts.android.myapplication.presentation.viewmodel.ProfileUiState
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.edit_profile
import myapplication.composeapp.generated.resources.logout
import myapplication.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
	state: ProfileUiState,
	onEditProfile: () -> Unit,
	onSettings: () -> Unit,
	onLogout: () -> Unit,
) {
	val user = state.user
	Scaffold { padding ->
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(bottom = padding.calculateBottomPadding()),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			item {
				// Hero Header Section
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.height(240.dp)
				) {
					// Avatar and Info
					Column(
						modifier = Modifier.fillMaxSize(),
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.Bottom
					) {
						Box(contentAlignment = Alignment.BottomEnd) {
							Surface(
								modifier = Modifier
									.size(120.dp)
									.padding(4.dp),
								shape = CircleShape,
								color = MaterialTheme.colorScheme.surface,
								shadowElevation = 4.dp,
								tonalElevation = 4.dp
							) {
								if (user?.photoUrl != null) {
									AsyncImage(
										model = ImageRequest.Builder(LocalPlatformContext.current)
											.data(user.photoUrl)
											.crossfade(true)
											.build(),
										contentDescription = null,
										modifier = Modifier.fillMaxSize().clip(CircleShape),
										contentScale = ContentScale.Crop,
									)
								} else {
									Icon(
										imageVector = Icons.Default.Person,
										contentDescription = null,
										modifier = Modifier.padding(24.dp),
										tint = MaterialTheme.colorScheme.onPrimaryContainer
									)
								}
							}
							// Verified Badge
							Surface(
								modifier = Modifier.size(32.dp),
								shape = CircleShape,
								color = MaterialTheme.colorScheme.primary,
								tonalElevation = 2.dp
							) {
								Icon(
									imageVector = Icons.Default.VerifiedUser,
									contentDescription = null,
									modifier = Modifier.padding(6.dp),
									tint = MaterialTheme.colorScheme.onPrimary
								)
							}
						}

						Spacer(Modifier.height(12.dp))

						Text(
							text = user?.displayName ?: "User",
							style = MaterialTheme.typography.headlineMedium,
							fontWeight = FontWeight.Black
						)
						Text(
							text = user?.email ?: "",
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
				}
			}

			item {
				Spacer(modifier = Modifier.height(24.dp))
				// Action Group
				Card(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp),
					colors = CardDefaults.cardColors(
						containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
							alpha = 0.3f
						)
					),
					shape = MaterialTheme.shapes.extraLarge
				) {
					Column {
						ProfileOptionItem(
							icon = Icons.Default.Edit,
							label = stringResource(Res.string.edit_profile),
							onClick = onEditProfile
						)
						HorizontalDivider(
							modifier = Modifier.padding(horizontal = 16.dp),
							thickness = 0.5.dp,
							color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
						)
						ProfileOptionItem(
							icon = Icons.Default.Settings,
							label = stringResource(Res.string.settings),
							onClick = onSettings
						)
					}
				}
			}

			item {
				Spacer(modifier = Modifier.height(32.dp))
				Button(
					onClick = onLogout,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp)
						.height(56.dp),
					shape = MaterialTheme.shapes.large,
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.errorContainer,
						contentColor = MaterialTheme.colorScheme.onErrorContainer
					)
				) {
					Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
					Spacer(modifier = Modifier.width(8.dp))
					Text(stringResource(Res.string.logout), fontWeight = FontWeight.Bold)
				}
				Spacer(modifier = Modifier.height(48.dp))
			}
		}
	}
}

@Composable
private fun ProfileOptionItem(
	icon: ImageVector,
	label: String,
	onClick: () -> Unit,
) {
	Surface(
		onClick = onClick,
		modifier = Modifier.fillMaxWidth(),
		color = Color.Transparent
	) {
		Row(
			modifier = Modifier
				.padding(vertical = 20.dp, horizontal = 20.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Surface(
				modifier = Modifier.size(40.dp),
				shape = CircleShape,
				color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
			) {
				Icon(
					imageVector = icon,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary,
					modifier = Modifier.padding(10.dp)
				)
			}
			Spacer(modifier = Modifier.width(16.dp))
			Text(
				text = label,
				style = MaterialTheme.typography.bodyLarge,
				fontWeight = FontWeight.Bold
			)
			Spacer(modifier = Modifier.weight(1f))
			Icon(
				imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
			)
		}
	}
}

@Preview(showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
	AppTheme {
		ProfileScreen(
			state = ProfileUiState(
				user = AuthUser(
					id = "user123",
					email = "duy.truong@example.com",
					displayName = "Duy Truong",
					photoUrl = null
				)
			),
			onEditProfile = {},
			onSettings = {},
			onLogout = {}
		)
	}
}
