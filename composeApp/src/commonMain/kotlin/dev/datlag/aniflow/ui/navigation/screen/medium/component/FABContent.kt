package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GetApp
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.type.MediaListStatus
import dev.datlag.aniflow.anilist.type.MediaStatus
import dev.datlag.aniflow.common.icon
import dev.datlag.aniflow.common.isScrollingUp
import dev.datlag.aniflow.common.mapCollect
import dev.datlag.aniflow.common.stringRes
import dev.datlag.aniflow.ui.custom.InstantAppContent
import dev.datlag.aniflow.ui.custom.speeddial.FABItem
import dev.datlag.aniflow.ui.custom.speeddial.SpeedDialFAB
import dev.datlag.aniflow.ui.custom.speeddial.SubSpeedDialFABs
import dev.datlag.aniflow.ui.custom.speeddial.rememberSpeedDialState
import dev.datlag.aniflow.ui.navigation.screen.medium.MediumComponent
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun FABContent(
    expanded: Boolean,
    component: MediumComponent
) {
    InstantAppContent(
        onInstantApp = { helper ->
            ExtendedFloatingActionButton(
                onClick = { helper.showInstallPrompt() },
                expanded = expanded,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.GetApp,
                        contentDescription = null,
                    )
                },
                text = {
                    Text(text = stringResource(SharedRes.strings.install))
                }
            )
        }
    ) {
        val notReleased by component.status.mapCollect(component.initialMedium.status) {
            it == MediaStatus.UNKNOWN__ || it == MediaStatus.NOT_YET_RELEASED
        }

        if (!notReleased) {
            val loggedIn by component.isLoggedIn.collectAsStateWithLifecycle(false)
            val status by component.listStatus.collectAsStateWithLifecycle(component.initialMedium.entry?.status ?: MediaListStatus.UNKNOWN__)
            val type by component.type.collectAsStateWithLifecycle(component.initialMedium.type)
            val uriHandler = LocalUriHandler.current
            val speedDialFABState = rememberSpeedDialState()

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val bsAvailable = component.bsAvailable
                val bsOptions by component.bsOptions.collectAsStateWithLifecycle(emptySet())
                val bsState = rememberUseCaseState()

                val rating by component.rating.collectAsStateWithLifecycle(-1)
                val ratingState = rememberUseCaseState()

                BSDialog(
                    state = bsState,
                    bsOptions = bsOptions,
                    onSearch = component::searchBS
                )

                RatingDialog(
                    state = ratingState,
                    initialValue = rating,
                    type = type,
                    onRating = {
                        component.rate(it)
                    }
                )

                SubSpeedDialFABs(
                    state = speedDialFABState,
                    items = listOfNotNull(
                        FABItem(
                            icon = Icons.Rounded.PlayArrow,
                            label = stringResource(status.stringRes(type)),
                            onClick = {
                                speedDialFABState.collapse()
                                component.edit()
                            }
                        ),
                        FABItem(
                            icon = Icons.Rounded.Star,
                            label = "Rating",
                            onClick = {
                                speedDialFABState.collapse()
                                ratingState.show()
                            }
                        ),
                        if (bsAvailable && bsOptions.isNotEmpty()) {
                            FABItem(
                                painter = painterResource(SharedRes.images.bs),
                                label = stringResource(SharedRes.strings.bs),
                                tint = true,
                                onClick = {
                                    speedDialFABState.collapse()
                                    bsState.show()
                                }
                            )
                        } else {
                            null
                        }
                    ),
                    showLabels = expanded
                )
                SpeedDialFAB(
                    state = speedDialFABState,
                    expanded = expanded,
                    onClick = {
                        if (!loggedIn) {
                            uriHandler.openUri(component.loginUri)
                        } else {
                            it.changeState()
                        }
                    },
                    iconRotation = 0F,
                    icon = {
                        Icon(
                            imageVector = status.icon(),
                            contentDescription = null,
                        )
                    },
                    text = {
                        Text(text = stringResource(status.stringRes(type)))
                    }
                )
            }
        }
    }
}