package com.duyts.pokerhost.presentation.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePickerLauncher(
	onImagePicked: (ByteArray) -> Unit,
): ImagePickerLauncher {
	return remember {
		object : ImagePickerLauncher {
			override fun launch() {
				// TODO: Implement iOS Image Picker
			}
		}
	}
}
