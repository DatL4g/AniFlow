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
import androidx.compose.runtime.*
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
import com.mikepenz.markdown.m3.Markdown
import dev.chrisbanes.haze.haze
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.common.htmlToAnnotatedString
import dev.datlag.aniflow.common.plus
import dev.datlag.aniflow.common.toComposeColor
import dev.datlag.aniflow.common.toComposeString
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.tooling.compose.onClick
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.update
import dev.datlag.aniflow.settings.model.Color as SettingsColor
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle
import dev.datlag.aniflow.settings.model.CharLanguage as SettingsChar

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
                        Markdown(
                            modifier = Modifier.padding(bottom = 16.dp),
                            content = it
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
            val temporaryColor by StateSaver.temporaryColor.collectAsStateWithLifecycle()
            val useCase = rememberUseCaseState(
                onFinishedRequest = {
                    StateSaver.updateTemporaryColor(null)
                },
                onCloseRequest = {
                    StateSaver.updateTemporaryColor(null)
                },
                onDismissRequest = {
                    StateSaver.updateTemporaryColor(null)
                }
            )
            val colors = remember { SettingsColor.all.toList() }

            OptionDialog(
                state = useCase,
                selection = OptionSelection.Single(
                    options = colors.map {
                        Option(
                            icon = IconSource(
                                imageVector = Icons.Filled.Circle,
                                tint = it.toComposeColor()
                            ),
                            selected = if (temporaryColor != null) {
                                it == temporaryColor
                            } else {
                                it == selectedColor
                            },
                            titleText = stringResource(it.toComposeString()),
                            onClick = {
                                StateSaver.updateTemporaryColor(it)
                            }
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
            val languages = remember { SettingsTitle.all.toList() }

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
            val selectedChar by component.selectedCharLanguage.collectAsStateWithLifecycle(null)
            val useCase = rememberUseCaseState()
            val languages = remember { SettingsChar.all.toList() }

            OptionDialog(
                state = useCase,
                selection = OptionSelection.Single(
                    options = languages.map {
                        Option(
                            selected = it == selectedChar,
                            titleText = stringResource(it.toComposeString())
                        )
                    },
                    onSelectOption = { option, _ ->
                        component.changeCharLanguage(languages[option])
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
                    imageVector = Icons.Filled.PersonPin,
                    contentDescription = null
                )
                Text(
                    text = "Character Language"
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