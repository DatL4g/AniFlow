package dev.datlag.aniflow.ui.custom

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.SharedRes
import dev.datlag.tooling.compose.withDefaultContext
import dev.datlag.tooling.compose.withIOContext
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay

@Composable
fun EditFAB(
    displayAdd: Boolean = false,
    bsAvailable: Boolean = false,
    expanded: Boolean = false,
    onBS: () -> Unit,
    onRate: () -> Unit,
    onProgress: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        var showOtherFABs by remember(expanded) { mutableStateOf(expanded) }

        AnimatedVisibility(
            visible = showOtherFABs,
            enter = slideInVertically(
                animationSpec = bouncySpring(),
                initialOffsetY = { -(-it / 2) }
            ) + fadeIn(
                animationSpec = bouncySpring()
            ),
            exit = slideOutVertically(
                animationSpec = bouncySpring(),
                targetOffsetY = { -(-it / 2) }
            ) + fadeOut(
                animationSpec = bouncySpring()
            )
        ) {
            LabelFAB(
                onClick = {
                    showOtherFABs = false
                    onProgress()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(
            visible = showOtherFABs,
            enter = slideInVertically(
                animationSpec = bouncySpring(),
                initialOffsetY = { -(-it / 2) }
            ) + fadeIn(
                animationSpec = bouncySpring()
            ),
            exit = slideOutVertically(
                animationSpec = bouncySpring(),
                targetOffsetY = { -(-it / 2) }
            ) + fadeOut(
                animationSpec = bouncySpring()
            )
        ) {
            LabelFAB(
                onClick = {
                    showOtherFABs = false
                    onRate()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(
            visible = showOtherFABs && bsAvailable,
            enter = slideInVertically(
                animationSpec = bouncySpring(),
                initialOffsetY = { -(-it / 2) }
            ) + fadeIn(
                animationSpec = bouncySpring()
            ),
            exit = slideOutVertically(
                animationSpec = bouncySpring(),
                targetOffsetY = { -(-it / 2) }
            ) + fadeOut(
                animationSpec = bouncySpring()
            )
        ) {
            LabelFAB(
                onClick = {
                    showOtherFABs = false
                    onBS()
                }
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(SharedRes.images.bs),
                    contentDescription = stringResource(SharedRes.strings.bs),
                    colorFilter = ColorFilter.tint(LocalContentColor.current)
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = {
                showOtherFABs = !showOtherFABs
            },
            expanded = expanded,
            icon = {
                val icon = if (displayAdd) {
                    Icons.Default.Add
                } else {
                    Icons.Default.Edit
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            },
            text = {
                val text = if (displayAdd) {
                    SharedRes.strings.add
                } else {
                    SharedRes.strings.edit
                }

                Text(text = stringResource(text))
            }
        )
    }
}

@Composable
private fun LabelFAB(
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    var showLabel by remember { mutableStateOf(false) }

    LaunchedEffect(showLabel) {
        if (!showLabel) {
            showLabel = true
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SmallFloatingActionButton(
            modifier = Modifier.padding(end = 4.dp),
            onClick = onClick
        ) {
            icon()
        }
    }

    DisposableEffect(showLabel) {
        onDispose {
            showLabel = false
        }
    }
}

private fun <T> bouncySpring() = spring<T>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)