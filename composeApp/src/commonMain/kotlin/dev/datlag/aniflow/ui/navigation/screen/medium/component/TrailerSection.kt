package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun TrailerSection(
    initialMedium: Medium,
    trailerFlow: Flow<Medium.Trailer?>,
    modifier: Modifier = Modifier
) {
    val trailer by trailerFlow.collectAsStateWithLifecycle(initialMedium.trailer)

    if (trailer != null) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val uriHandler = LocalUriHandler.current

            Text(
                modifier = Modifier.padding(top = 16.dp).padding(horizontal = 16.dp),
                text = stringResource(SharedRes.strings.trailer),
                style = MaterialTheme.typography.headlineSmall
            )
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp).padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                onClick = {
                    uriHandler.openUri(trailer?.videoUrl ?: trailer!!.website)
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = trailer?.thumbnail,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    if (trailer?.isYoutube == true) {
                        Image(
                            modifier = Modifier.size(48.dp),
                            painter = painterResource(SharedRes.images.youtube),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(
                                Color(
                                    alpha = 200,
                                    red = 205,
                                    green = 32,
                                    blue = 31
                                )
                            )
                        )
                    } else {
                        Icon(
                            modifier = Modifier.size(48.dp),
                            imageVector = Icons.Filled.PlayCircleFilled,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}