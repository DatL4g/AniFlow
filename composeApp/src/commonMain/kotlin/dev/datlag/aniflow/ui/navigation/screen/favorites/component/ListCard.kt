package dev.datlag.aniflow.ui.navigation.screen.favorites.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.settings.model.TitleLanguage
import kotlin.math.max
import kotlin.math.min

@Composable
fun ListCard(
    medium: Medium,
    titleLanguage: TitleLanguage?,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = { },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier.widthIn(min = 100.dp, max = 120.dp).fillMaxHeight().clip(MaterialTheme.shapes.medium),
                model = medium.coverImage.extraLarge,
                contentDescription = medium.preferred(titleLanguage),
                contentScale = ContentScale.Crop,
                error = rememberAsyncImagePainter(
                    model = medium.coverImage.large,
                    contentScale = ContentScale.Crop,
                    error = rememberAsyncImagePainter(
                        model = medium.coverImage.medium,
                        contentScale = ContentScale.Crop,
                        onSuccess = { state ->
                        }
                    ),
                    onSuccess = { state ->
                    }
                ),
                onSuccess = { state ->
                }
            )
            Column(
                modifier = Modifier.weight(1F).padding(top = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val progress = medium.entry?.progress ?: 0
                val maxProgress = max(progress, medium.episodes)

                Text(
                    text = medium.preferred(titleLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = true,
                    modifier = Modifier.fillMaxWidth().weight(1F),
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LinearProgressIndicator(
                        progress = { 1F },
                        modifier = Modifier
                            .fillMaxWidth(
                                fraction = (progress.toFloat() / maxProgress.toFloat())
                            )
                            .clip(CircleShape)
                    )
                    LinearProgressIndicator(
                        progress = { 0F },
                        modifier = Modifier.weight(1F).clip(CircleShape)
                    )
                }
            }
        }
    }
}