package dev.datlag.aniflow.ui.navigation.screen.initial.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.haze
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.common.plus
import dev.datlag.aniflow.other.Constants
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.ui.navigation.screen.initial.settings.component.*
import dev.datlag.tooling.compose.onClick
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.painterResource

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
            UserSection(
                userFlow = component.user,
                loginUri = component.loginUri,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
        item {
            ColorSection(
                selectedColorFlow = component.selectedColor,
                modifier = Modifier.fillParentMaxWidth(),
                onChange = component::changeProfileColor
            )
        }
        item {
            TitleSection(
                titleFlow = component.selectedTitleLanguage,
                modifier = Modifier.fillParentMaxWidth(),
                onChange = component::changeTitleLanguage
            )
        }
        item {
            CharacterSection(
                characterFlow = component.selectedCharLanguage,
                modifier = Modifier.fillParentMaxWidth(),
                onChanged = component::changeCharLanguage
            )
        }
        item {
            AdultSection(
                adultFlow = component.adultContent,
                modifier = Modifier.fillParentMaxWidth(),
                onChange = component::changeAdultContent
            )
        }
        item {
            DomainSection(
                modifier = Modifier.fillParentMaxWidth()
            )
        }
        item {
            val uriHandler = LocalUriHandler.current
            val isLoggedIn by component.isLoggedIn.collectAsStateWithLifecycle(false)

            Row(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .defaultMinSize(minHeight = ButtonDefaults.MinHeight)
                    .clip(MaterialTheme.shapes.small)
                    .onClick {
                        if (isLoggedIn) {
                            component.logout()
                        } else {
                            uriHandler.openUri(component.loginUri)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isLoggedIn) {
                    Icon(
                        imageVector = Icons.Default.NotInterested,
                        contentDescription = null,
                    )
                    Text(text = "Logout")
                } else {
                    Image(
                        modifier = Modifier.size(24.dp).clip(CircleShape),
                        painter = painterResource(SharedRes.images.anilist),
                        contentDescription = null,
                    )
                    Text(text = "Login")
                }
            }
        }
        item {
            val uriHandler = LocalUriHandler.current

            Row(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .defaultMinSize(minHeight = ButtonDefaults.MinHeight)
                    .clip(MaterialTheme.shapes.small)
                    .onClick {
                        uriHandler.openUri(Constants.GITHUB_REPO)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(SharedRes.images.github),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalContentColor.current)
                )
                Text(text = "GitHub Repository")
            }
        }
        item {
            val uriHandler = LocalUriHandler.current

            Row(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .defaultMinSize(minHeight = ButtonDefaults.MinHeight)
                    .clip(MaterialTheme.shapes.medium)
                    .onClick {
                        uriHandler.openUri(Constants.GITHUB_OWNER)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                )
                Text(text = "Developed by DatLag")
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