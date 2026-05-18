package com.duyts.pokerhost.presentation.ui.profile

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(
	onImagePicked: (ByteArray) -> Unit,
): ImagePickerLauncher

interface ImagePickerLauncher {
	fun launch()
}
