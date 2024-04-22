package dev.datlag.aniflow.ui.navigation.screen.medium.dialog.character

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Man4
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.CharacterStateMachine
import dev.datlag.aniflow.common.htmlToAnnotatedString
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.common.preferredName
import dev.datlag.aniflow.ui.navigation.screen.medium.component.TranslateButton
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharacterDialog(component: CharacterComponent) {
    AlertDialog(
        onDismissRequest = component::dismiss,
        icon = {
            val image by component.image.collectAsStateWithLifecycle()

            AsyncImage(
                modifier = Modifier.size(80.dp).clip(CircleShape),
                model = image.large,
                error = rememberAsyncImagePainter(
                    model = image.medium,
                    contentScale = ContentScale.Crop,
                ),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                contentDescription = component.initialChar.preferredName()
            )
        },
        title = {
            val name by component.name.collectAsStateWithLifecycle()

            Text(
                text = name.preferred(),
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
                softWrap = true
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val description by component.description.collectAsStateWithLifecycle()
                val translatedDescription by component.translatedDescription.collectAsStateWithLifecycle()

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
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
                    Text(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
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
        },
        dismissButton = {
            val state by component.state.collectAsStateWithLifecycle()
            val description by component.description.collectAsStateWithLifecycle()

            description?.let {
                TranslateButton(
                    text = it,
                ) { text ->
                    component.descriptionTranslation(text)
                }
            } ?: run {
                if (state is CharacterStateMachine.State.Error) {
                    TextButton(
                        onClick = component::retry
                    ) {
                        Text(text = stringResource(SharedRes.strings.retry))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = component::dismiss
            ) {
                Text(text = stringResource(SharedRes.strings.close))
            }
        }
    )
}