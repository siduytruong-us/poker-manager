package com.duyts.pokerhost.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.duyts.pokerhost.domain.repository.AuthUser
import com.duyts.pokerhost.presentation.theme.AppTheme
import com.duyts.pokerhost.presentation.viewmodel.EditProfileUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pokerhost.composeapp.generated.resources.Res
import pokerhost.composeapp.generated.resources.cancel
import pokerhost.composeapp.generated.resources.compose_multiplatform
import pokerhost.composeapp.generated.resources.confirm
import pokerhost.composeapp.generated.resources.edit_profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
	user: AuthUser?,
	state: EditProfileUiState,
	onSave: (displayName: String, photoUrl: String?, imageBytes: ByteArray?) -> Unit,
	onBack: () -> Unit,
	onResetState: () -> Unit,
) {
	var displayName by remember(user) { mutableStateOf(user?.displayName ?: "") }
	var photoUrl by remember(user) { mutableStateOf(user?.photoUrl ?: "") }
	var pickedImageBytes by remember { mutableStateOf<ByteArray?>(null) }

	val imagePicker = rememberImagePickerLauncher { bytes ->
		pickedImageBytes = bytes
	}

	LaunchedEffect(state) {
		if (state is EditProfileUiState.Success) {
			onResetState()
			onBack()
		}
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(Res.string.edit_profile)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(Res.string.cancel)
						)
					}
				}
			)
		}
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			// Avatar
			Box(
				modifier = Modifier
					.size(120.dp),
				contentAlignment = Alignment.Center
			) {
				Box(
					modifier = Modifier
						.size(120.dp)
						.clip(CircleShape)
						.background(MaterialTheme.colorScheme.primaryContainer),
					contentAlignment = Alignment.Center
				) {
					val model = remember(pickedImageBytes, user?.photoUrl) {
						pickedImageBytes ?: user?.photoUrl
					}

					if (model != null) {
						AsyncImage(
							model = ImageRequest.Builder(LocalPlatformContext.current)
								.data(model)
								.crossfade(true)
								.build(),
							contentDescription = null,
							modifier = Modifier.fillMaxSize(),
							contentScale = ContentScale.Crop,
							error = painterResource(Res.drawable.compose_multiplatform),
							placeholder = painterResource(Res.drawable.compose_multiplatform)
						)
					} else {
						Icon(
							imageVector = Icons.Default.Person,
							contentDescription = null,
							modifier = Modifier.size(64.dp),
							tint = MaterialTheme.colorScheme.onPrimaryContainer
						)
					}
				}

				// Edit Icon
				IconButton(
					onClick = { imagePicker.launch() },
					modifier = Modifier
						.align(Alignment.BottomEnd)
						.size(32.dp)
						.clip(CircleShape)
						.background(MaterialTheme.colorScheme.primary),
				) {
					Icon(
						imageVector = Icons.Default.Edit,
						contentDescription = "Change Avatar",
						modifier = Modifier.size(20.dp),
						tint = MaterialTheme.colorScheme.onPrimary
					)
				}
			}

			Spacer(modifier = Modifier.height(8.dp))

			TextField(
				value = displayName,
				onValueChange = { displayName = it },
				label = { Text("Display Name") },
				modifier = Modifier.fillMaxWidth(),
				singleLine = true
			)

			if (state is EditProfileUiState.Error) {
				Text(
					text = state.message,
					color = MaterialTheme.colorScheme.error,
					style = MaterialTheme.typography.bodySmall
				)
			}

			Spacer(modifier = Modifier.weight(1f))

			Button(
				onClick = {
					onSave(displayName, photoUrl.ifBlank { null }, pickedImageBytes)
				},
				modifier = Modifier.fillMaxWidth(),
				enabled = state !is EditProfileUiState.Loading && displayName.isNotBlank()
			) {
				if (state is EditProfileUiState.Loading) {
					CircularProgressIndicator(
						modifier = Modifier.size(24.dp),
						color = MaterialTheme.colorScheme.onPrimary,
						strokeWidth = 2.dp
					)
				} else {
					Text(stringResource(Res.string.confirm))
				}
			}
		}
	}
}

@Preview
@Composable
fun EditProfileScreenPreview() {
	AppTheme {
		EditProfileScreen(
			user = AuthUser(
				id = "1",
				email = "test@example.com",
				displayName = "John Doe",
				photoUrl = "https://example.com/photo.jpg"
			),
			state = EditProfileUiState.Idle,
			onSave = { _, _, _ -> },
			onBack = {},
			onResetState = {}
		)
	}
}

@Preview
@Composable
fun EditProfileScreenLoadingPreview() {
	AppTheme {
		EditProfileScreen(
			user = AuthUser(
				id = "1",
				email = "test@example.com",
				displayName = "John Doe",
				photoUrl = null
			),
			state = EditProfileUiState.Loading,
			onSave = { _, _, _ -> },
			onBack = {},
			onResetState = {}
		)
	}
}
