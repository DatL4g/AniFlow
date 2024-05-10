package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.NoAdultContent
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.rounded.NoAdultContent
import androidx.compose.material.icons.rounded.RssFeed
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.MediaFormat
import dev.datlag.aniflow.anilist.type.MediaStatus
import dev.datlag.aniflow.common.icon
import dev.datlag.aniflow.common.text
import dev.datlag.tooling.compose.ifTrue
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CoverSection(
    coverImage: Medium.CoverImage,
    initialMedium: Medium,
    formatFlow: Flow<MediaFormat>,
    episodesFlow: Flow<Int>,
    durationFlow: Flow<Int>,
    statusFlow: Flow<MediaStatus>,
    isAdultFlow: Flow<Boolean>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var coverShadow by remember(coverImage) { mutableStateOf(false) }

        AsyncImage(
            modifier = Modifier
                .width(140.dp)
                .height(200.dp)
                .ifTrue(coverShadow) {
                    shadow(
                        elevation = 8.dp,
                        shape = MaterialTheme.shapes.medium,
                        spotColor = MaterialTheme.colorScheme.primary
                    )
                },
            model = coverImage.extraLarge,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            error = rememberAsyncImagePainter(
                model = coverImage.large,
                contentScale = ContentScale.Crop,
                error = rememberAsyncImagePainter(
                    model = coverImage.medium,
                    contentScale = ContentScale.Crop,
                    onSuccess = {
                        coverShadow = true
                    }
                ),
                onSuccess = {
                    coverShadow = true
                }
            ),
            onSuccess = {
                coverShadow = true
            }
        )
        Column(
            modifier = Modifier.weight(1F).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            val format by formatFlow.collectAsStateWithLifecycle(initialMedium.format)
            val episodes by episodesFlow.collectAsStateWithLifecycle(initialMedium.episodes)
            val duration by durationFlow.collectAsStateWithLifecycle(initialMedium.avgEpisodeDurationInMin)
            val status by statusFlow.collectAsStateWithLifecycle(initialMedium.status)
            val isAdult by isAdultFlow.collectAsStateWithLifecycle(initialMedium.isAdult)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = format.icon(),
                    contentDescription = null
                )
                Text(text = stringResource(format.text()))
            }
            if (isAdult) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NoAdultContent,
                        contentDescription = null
                    )
                    Text(text = stringResource(SharedRes.strings.explicit))
                }
            }
            if (episodes > -1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.List,
                        contentDescription = null
                    )
                    Text(text = "$episodes Episodes")
                }
            }
            if (duration > -1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Timelapse,
                        contentDescription = null
                    )
                    Text(text = "${duration}min / Episode")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.RssFeed,
                    contentDescription = null
                )
                Text(text = stringResource(status.text()))
            }
        }
    }
}