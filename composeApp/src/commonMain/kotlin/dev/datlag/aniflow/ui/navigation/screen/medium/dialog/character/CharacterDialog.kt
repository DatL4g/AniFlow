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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.LocalEdgeToEdge
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.CharacterStateMachine
import dev.datlag.aniflow.common.*
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
        val name by component.name.collectAsStateWithLifecycle()

        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            val image by component.image.collectAsStateWithLifecycle()
            val state by component.state.collectAsStateWithLifecycle()

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
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = stringResource(SharedRes.strings.close)
                    )
                }
            }

            AsyncImage(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
                model = image.large,
                error = rememberAsyncImagePainter(
                    model = image.medium,
                    contentScale = ContentScale.Crop,
                    placeholder = shimmerPainter()
                ),
                placeholder = shimmerPainter(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                contentDescription = component.initialChar.preferredName()
            )

            this@ModalBottomSheet.AnimatedVisibility(
                modifier = Modifier.align(Alignment.CenterEnd),
                visible = state.isSuccess,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val isFavoriteBlocked by component.isFavoriteBlocked.collectAsStateWithLifecycle()
                val isFavorite by component.isFavorite.collectAsStateWithLifecycle()
                var favoriteChanged by remember(isFavorite) { mutableStateOf<Boolean?>(null) }

                IconButton(
                    onClick = {
                        favoriteChanged = !(favoriteChanged ?: isFavorite)
                        component.toggleFavorite()
                    },
                    enabled = !isFavoriteBlocked
                ) {
                    Icon(
                        imageVector = if (favoriteChanged ?: isFavorite) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = null,
                    )
                }
            }
        }

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
            text = name.preferred(),
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
            val description by component.description.collectAsStateWithLifecycle()
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
                val gender by component.gender.collectAsStateWithLifecycle()
                val bloodType by component.bloodType.collectAsStateWithLifecycle()
                val birthDate by component.birthDate.collectAsStateWithLifecycle()

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
            } ?: run {
                val state by component.state.collectAsStateWithLifecycle()

                if (state is CharacterStateMachine.State.Loading) {
                    CircularProgressIndicator()
                }
                // ToDo("Display something went wrong")
            }
        }
    }
}