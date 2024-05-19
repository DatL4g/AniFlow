package dev.datlag.aniflow.ui.navigation.screen.discover.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.ui.theme.SchemeTheme
import dev.datlag.tooling.compose.onClick

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun SearchResult(
    medium: Medium,
    modifier: Modifier = Modifier,
    onClick: (Medium) -> Unit
) {
    Row(
        modifier = modifier.clip(CardDefaults.shape).onClick { onClick(medium) },
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val defaultColor = remember(medium.coverImage.color) {
            medium.coverImage.color?.substringAfter('#')?.let {
                val colorValue = it.hexToLong() or 0x00000000FF000000
                Color(colorValue)
            }
        }

        val updater = SchemeTheme.create(
            key = medium.id,
            defaultColor = defaultColor,
        )

        AsyncImage(
            modifier = Modifier
                .width(100.dp)
                .height(140.dp)
                .clip(MaterialTheme.shapes.medium),
            model = medium.coverImage.extraLarge,
            contentDescription = medium.preferred(null),
            contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter(
                model = medium.coverImage.large,
                contentScale = ContentScale.Crop,
                error = rememberAsyncImagePainter(
                    model = medium.coverImage.medium,
                    contentScale = ContentScale.Crop,
                    onSuccess = { state ->
                        updater?.update(state.painter)
                    }
                ),
                onSuccess = { state ->
                    updater?.update(state.painter)
                }
            ),
            onSuccess = { state ->
                updater?.update(state.painter)
            }
        )
        Column(
            modifier = Modifier.weight(1F).padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = medium.preferred(null),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}