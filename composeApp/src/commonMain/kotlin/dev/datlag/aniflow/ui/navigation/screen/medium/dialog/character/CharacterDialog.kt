package dev.datlag.aniflow.ui.navigation.screen.medium.dialog.character

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.LocalEdgeToEdge
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.CharacterRepository
import dev.datlag.aniflow.common.*
import dev.datlag.aniflow.other.rememberInstantAppHelper
import dev.datlag.aniflow.ui.navigation.screen.medium.component.TranslateButton
import dev.datlag.tooling.compose.ifFalse
import dev.datlag.tooling.compose.ifTrue
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CharacterDialog(component: CharacterComponent) {
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
        val name by component.name.collectAsStateWithLifecycle(component.initialChar.name)
        val charLanguage by component.charLanguage.collectAsStateWithLifecycle(null)

        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            val image by component.image.collectAsStateWithLifecycle(component.initialChar.image)
            val state by component.state.collectAsStateWithLifecycle(null)
            val instantAppHelper = rememberInstantAppHelper()

            this@ModalBottomSheet.AnimatedVisibility(
                modifier = Modifier.align(Alignment.CenterStart),
                visible = sheetState.isFullyExpandedOrTargeted(forceFullExpand = true),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = component::dismiss
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = stringResource(SharedRes.strings.close)
                    )
                }
            }

            AsyncImage(
                modifier = Modifier
                    .padding(12.dp)
                    .size(96.dp)
                    .clip(CircleShape),
                model = image.large,
                error = rememberAsyncImagePainter(
                    model = image.medium,
                    contentScale = ContentScale.Crop
                ),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                contentDescription = component.initialChar.preferredName(charLanguage)
            )

            this@ModalBottomSheet.AnimatedVisibility(
                visible = state == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(108.dp)
                )
            }

            this@ModalBottomSheet.AnimatedVisibility(
                modifier = Modifier.align(Alignment.CenterEnd),
                visible = state is CharacterRepository.State.Success && !instantAppHelper.isInstantApp,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val loggedIn by component.isLoggedIn.collectAsStateWithLifecycle(false)
                val isFavoriteBlocked by component.isFavoriteBlocked.collectAsStateWithLifecycle(component.initialChar.isFavoriteBlocked)
                val isFavorite by component.isFavorite.collectAsStateWithLifecycle(component.initialChar.isFavorite)
                var favoriteChanged by remember(isFavorite) { mutableStateOf<Boolean?>(null) }
                val uriHandler = LocalUriHandler.current

                IconButton(
                    onClick = {
                        if (!loggedIn) {
                            uriHandler.openUri(component.loginUri)
                        } else {
                            favoriteChanged = !(favoriteChanged ?: isFavorite)
                            component.toggleFavorite()
                        }
                    },
                    enabled = !loggedIn || !isFavoriteBlocked
                ) {
                    Icon(
                        imageVector = if (favoriteChanged ?: isFavorite) {
                            Icons.Rounded.Favorite
                        } else {
                            Icons.Rounded.FavoriteBorder
                        },
                        contentDescription = null,
                    )
                }
            }
        }

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = name.preferred(charLanguage),
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.SemiBold,
            softWrap = true
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val description by component.description.collectAsStateWithLifecycle(component.initialChar.description)
            val translatedDescription by component.translatedDescription.collectAsStateWithLifecycle()
            val textHasPadding = remember(translatedDescription, description) {
                !translatedDescription.isNullOrBlank() || !description.isNullOrBlank()
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
                    .ifFalse(textHasPadding) {
                        padding(bottomPadding.merge(PaddingValues(bottom = 16.dp)))
                    },
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val gender by component.gender.collectAsStateWithLifecycle(component.initialChar.gender)
                val bloodType by component.bloodType.collectAsStateWithLifecycle(component.initialChar.bloodType)
                val birthDate by component.birthDate.collectAsStateWithLifecycle(component.initialChar.birthDate)

                bloodType?.let {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bloodtype,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(SharedRes.strings.blood_type),
                            style = MaterialTheme.typography.labelSmall,
                        )
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                gender?.let {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Man4,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(SharedRes.strings.gender),
                            style = MaterialTheme.typography.labelSmall,
                        )
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                birthDate?.let {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Cake,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(SharedRes.strings.birth_date),
                            style = MaterialTheme.typography.labelSmall,
                        )
                        Text(
                            text = it.format(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            (translatedDescription ?: description)?.let {
                TranslateButton(description ?: "") { text ->
                    component.descriptionTranslation(text)
                }
                Text(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(
                            bottomPadding.merge(PaddingValues(bottom = 16.dp))
                        ),
                    text = it.htmlToAnnotatedString()
                )
            }
        }
    }
}