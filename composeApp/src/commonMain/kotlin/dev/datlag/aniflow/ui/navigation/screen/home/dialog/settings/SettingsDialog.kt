package dev.datlag.aniflow.ui.navigation.screen.home.dialog.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.NotInterested
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.LocalEdgeToEdge
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.common.isFullyExpandedOrTargeted
import dev.datlag.aniflow.common.merge
import dev.datlag.aniflow.other.Constants
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.ui.navigation.screen.home.dialog.settings.component.*
import dev.datlag.tooling.compose.onClick
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(component: SettingsComponent) {
    val sheetState = rememberModalBottomSheetState()
    val (insets, bottomPadding) = if (LocalEdgeToEdge.current) {
        WindowInsets(
            left = 0,
            top = 0,
            right = 0,
            bottom = 0
        ) to BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Bottom).asPaddingValues()
    } else {
        BottomSheetDefaults.windowInsets to PaddingValues()
    }

    ModalBottomSheet(
        onDismissRequest = component::dismiss,
        windowInsets = insets,
        sheetState = sheetState
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = bottomPadding.merge(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                UserSection(
                    userFlow = component.user,
                    loginUri = component.loginUri,
                    dismissVisible = sheetState.isFullyExpandedOrTargeted(forceFullExpand = true),
                    modifier = Modifier.fillParentMaxWidth(),
                    onDismiss = component::dismiss
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
                            imageVector = Icons.Rounded.NotInterested,
                            contentDescription = null,
                        )
                        Text(text = stringResource(SharedRes.strings.logout))
                    } else {
                        Image(
                            modifier = Modifier.size(24.dp).clip(CircleShape),
                            painter = painterResource(SharedRes.images.anilist),
                            contentDescription = null,
                        )
                        Text(text = stringResource(SharedRes.strings.login))
                    }
                }
            }
            item {
                GitHubRepoSection(modifier = Modifier.fillParentMaxWidth())
            }
            item {
                GitHubOwnerSection(modifier = Modifier.fillParentMaxWidth())
            }
            item {
                NekosSection(
                    modifier = Modifier.fillParentMaxWidth(),
                    onClick = component::nekos
                )
            }
            item {
                SponsorSection(modifier = Modifier.fillParentMaxWidth())
            }
            item {
                AboutSection(
                    modifier = Modifier.fillParentMaxWidth(),
                    onClick = component::about
                )
            }
            item {
                PrivacySection(
                    modifier = Modifier.fillParentMaxWidth().padding(top = 16.dp, bottom = 4.dp)
                )
            }
        }
    }
}