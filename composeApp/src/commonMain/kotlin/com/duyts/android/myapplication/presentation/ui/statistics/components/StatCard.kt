package com.duyts.android.myapplication.presentation.ui.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
	Card(
		modifier = modifier,
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
				alpha = 0.5f
			)
		),
		shape = MaterialTheme.shapes.large
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Surface(
				modifier = Modifier.size(32.dp),
				shape = CircleShape,
				color = iconColor.copy(alpha = 0.1f)
			) {
				Icon(
					imageVector = icon,
					contentDescription = null,
					modifier = Modifier.padding(6.dp),
					tint = iconColor
				)
			}
			Column {
				Text(
					text = label,
					style = MaterialTheme.typography.labelMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				Text(
					text = value,
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Black,
					color = valueColor,
					fontSize = 18.sp
				)
			}
		}
	}
}
