package dev.datlag.aniflow.ui.navigation.screen.initial.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.More
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.option.OptionDialog
import com.maxkeppeler.sheets.option.models.DisplayMode
import com.maxkeppeler.sheets.option.models.Option
import com.maxkeppeler.sheets.option.models.OptionConfig
import com.maxkeppeler.sheets.option.models.OptionSelection
import dev.chrisbanes.haze.haze
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.common.htmlToAnnotatedString
import dev.datlag.aniflow.common.plus
import dev.datlag.aniflow.common.toComposeColor
import dev.datlag.aniflow.common.toComposeString
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.tooling.compose.onClick
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
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
                    u.description?.let {
                        Text(
                            modifier = Modifier.padding(bottom = 8.dp),
                            text = it.htmlToAnnotatedString()
                        )
                    }
                }
            } ?: Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            val selectedColor by component.selectedColor.collectAsStateWithLifecycle(null)
            val useCase = rememberUseCaseState()
            val colors = remember { AppSettings.Color.all.toList() }

            OptionDialog(
                state = useCase,
                selection = OptionSelection.Single(
                    options = colors.map {
                        Option(
                            icon = IconSource(
                                imageVector = Icons.Filled.Circle,
                                tint = it.toComposeColor()
                            ),
                            selected = it == selectedColor,
                            titleText = stringResource(it.toComposeString())
                        )
                    },
                    onSelectOption = { option, _ ->
                        component.changeProfileColor(colors[option])
                    }
                ),
                config = OptionConfig(
                    mode = DisplayMode.GRID_VERTICAL,
                    gridColumns = 4
                )
            )

            Row(
                modifier = Modifier.fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                )
                Text(
                    text = "Profile Color"
                )
                Spacer(modifier = Modifier.weight(1F))
                IconButton(
                    onClick = { useCase.show() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = null,
                        tint = selectedColor?.toComposeColor() ?: LocalContentColor.current
                    )
                }
            }
        }
        item {
            val selectedTitle by component.selectedTitleLanguage.collectAsStateWithLifecycle(null)
            val useCase = rememberUseCaseState()
            val languages = remember { AppSettings.TitleLanguage.all.toList() }

            OptionDialog(
                state = useCase,
                selection = OptionSelection.Single(
                    options = languages.map {
                        Option(
                            selected = it == selectedTitle,
                            titleText = stringResource(it.toComposeString())
                        )
                    },
                    onSelectOption = { option, _ ->
                        component.changeTitleLanguage(languages[option])
                    }
                ),
                config = OptionConfig(
                    mode = DisplayMode.LIST,
                )
            )

            Row(
                modifier = Modifier.fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Title,
                    contentDescription = null
                )
                Text(
                    text = "Title Language"
                )
                Spacer(modifier = Modifier.weight(1F))
                IconButton(
                    onClick = { useCase.show() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }
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