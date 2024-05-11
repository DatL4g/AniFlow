package dev.datlag.aniflow.ui.navigation.screen.home.component.airing

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.common.formatNext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun Airing(airingAt: Int, color: Color = LocalContentColor.current) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Schedule,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        val dateTime = remember(airingAt) {
            Instant.fromEpochSeconds(
                airingAt.toLong()
            ).toLocalDateTime(
                TimeZone.currentSystemDefault()
            )
        }
        Text(
            text = dateTime.formatNext(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = color
        )
    }
}