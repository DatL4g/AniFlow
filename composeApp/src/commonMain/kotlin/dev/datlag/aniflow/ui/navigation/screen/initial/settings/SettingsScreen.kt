package dev.datlag.aniflow.ui.navigation.screen.initial.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NoAdultContent
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.chrisbanes.haze.haze
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.common.plus
import dev.datlag.aniflow.common.toComposeColor
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.tooling.compose.onClick
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SettingsScreen(component: SettingsComponent) {
    val padding = PaddingValues(16.dp)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = StateSaver.List.settingsOverview,
        initialFirstVisibleItemScrollOffset = StateSaver.List.settingsOverviewOffset
    )

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().haze(state = LocalHaze.current),
        contentPadding = LocalPaddingValues.current?.plus(padding) ?: padding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            val user by component.user.collectAsStateWithLifecycle(null)

            user?.let { u ->
                Column(
                    modifier = Modifier.fillParentMaxWidth().padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        modifier = Modifier.size(96.dp).clip(CircleShape),
                        model = u.avatar.large,
                        contentDescription = null,
                        error = rememberAsyncImagePainter(
                            model = u.avatar.medium,
                            contentScale = ContentScale.Crop,
                        ),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                    Text(
                        text = u.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } ?: Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            val adultContent by component.adultContent.collectAsStateWithLifecycle(false)

            Row(
                modifier = Modifier.fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NoAdultContent,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(SharedRes.strings.adult_content_setting)
                )
                Spacer(modifier = Modifier.weight(1F))
                Switch(
                    checked = adultContent,
                    onCheckedChange = component::changeAdultContent,
                    thumbContent = {
                        if (adultContent) {
                            Icon(
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
        item {
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = "Profile Color",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        item {
            FlowRow(
                modifier = Modifier.fillParentMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                maxItemsInEachRow = when (calculateWindowSizeClass().widthSizeClass) {
                    WindowWidthSizeClass.Compact -> 4
                    else -> Int.MAX_VALUE
                }
            ) {
                AppSettings.Color.all.forEach {
                    ColorItem(
                        color = it,
                        onClick = { chosen ->
                            component.changeProfileColor(chosen)
                        }
                    )
                }
            }
        }
    }

    DisposableEffect(listState) {
        onDispose {
            StateSaver.List.settingsOverview = listState.firstVisibleItemIndex
            StateSaver.List.settingsOverviewOffset = listState.firstVisibleItemScrollOffset
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ColorItem(
    color: AppSettings.Color,
    onClick: (AppSettings.Color) -> Unit
) {
    Card(
        modifier = Modifier.size(48.dp),
        onClick = { onClick(color) },
        colors = CardDefaults.cardColors(containerColor = color.toComposeColor()),
        shape = CircleShape,
        content = { }
    )
}