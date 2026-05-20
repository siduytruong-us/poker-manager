package com.duyts.pokerhost.presentation.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.duyts.pokerhost.presentation.theme.AppTheme
import com.duyts.pokerhost.presentation.theme.ThemePreviewProvider
import com.duyts.pokerhost.presentation.viewmodel.SettingsUiState
import com.duyts.pokerhost.util.Language
import org.jetbrains.compose.resources.stringResource
import pokerhost.composeapp.generated.resources.Res
import pokerhost.composeapp.generated.resources.cancel
import pokerhost.composeapp.generated.resources.dark_mode
import pokerhost.composeapp.generated.resources.language
import pokerhost.composeapp.generated.resources.settings
import pokerhost.composeapp.generated.resources.toggle_theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
	state: SettingsUiState,
	onBack: () -> Unit,
	onDarkModeToggle: (Boolean) -> Unit,
	onLanguageChange: (Language) -> Unit,
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(Res.string.settings)) },
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
			verticalArrangement = Arrangement.spacedBy(24.dp)
		) {
			// Dark Mode Section
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Column {
					Text(
						stringResource(Res.string.dark_mode),
						style = MaterialTheme.typography.titleMedium
					)
					Text(
						stringResource(Res.string.toggle_theme),
						style = MaterialTheme.typography.bodySmall
					)
				}
				Switch(
					checked = state.isDarkMode,
					onCheckedChange = onDarkModeToggle
				)
			}

			HorizontalDivider()

			// Language Section
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Text(
					stringResource(Res.string.language),
					style = MaterialTheme.typography.titleMedium
				)
				Language.entries.forEach { language ->
					Row(
						modifier = Modifier.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically
					) {
						RadioButton(
							selected = state.language == language,
							onClick = { onLanguageChange(language) }
						)
						Text(
							text = language.displayName,
							modifier = Modifier.padding(start = 8.dp),
							style = MaterialTheme.typography.bodyLarge
						)
					}
				}
			}
		}
	}
}

@Preview(showSystemUi = true)
@Composable
fun SettingsScreenPreview(
	@PreviewParameter(ThemePreviewProvider::class) darkTheme: Boolean,
) {
	AppTheme(darkTheme = darkTheme) {
		SettingsScreen(
			state = SettingsUiState(
				isDarkMode = darkTheme,
				language = Language.ENGLISH
			),
			onBack = {},
			onDarkModeToggle = {},
			onLanguageChange = {}
		)
	}
}
