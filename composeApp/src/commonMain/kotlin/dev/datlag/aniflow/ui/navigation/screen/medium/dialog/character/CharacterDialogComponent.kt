package dev.datlag.aniflow.ui.navigation.screen.medium.dialog.character

import androidx.compose.runtime.Composable
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.arkivanov.decompose.ComponentContext
import dev.datlag.aniflow.anilist.CharacterRepository
import dev.datlag.aniflow.anilist.FavoriteToggleMutation
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.common.nullableFirebaseInstance
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.model.safeFirstOrNull
import dev.datlag.aniflow.other.Constants
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.settings.model.CharLanguage
import dev.datlag.aniflow.settings.model.TitleLanguage
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.decompose.ioScope
import dev.datlag.tooling.safeCast
import kotlinx.coroutines.flow.*
import org.kodein.di.DI
import org.kodein.di.instance

class CharacterDialogComponent(
    componentContext: ComponentContext,
    override val di: DI,
    override val initialChar: Character,
    private val onDismiss: () -> Unit
) : CharacterComponent, ComponentContext by componentContext {

    private val aniListClient by di.instance<ApolloClient>(Constants.AniList.APOLLO_CLIENT)
    private val characterRepository by di.instance<CharacterRepository>()

    private val appSettings by di.instance<Settings.PlatformAppSettings>()
    override val charLanguage: Flow<CharLanguage?> = appSettings.charLanguage.flowOn(ioDispatcher())

    override val state = characterRepository.character
    private val characterSuccessState = state.mapNotNull {
        it.safeCast<CharacterRepository.State.Success>()
    }

    override val image: Flow<Character.Image> = characterSuccessState.map {
        it.character.image
    }

    override val name: Flow<Character.Name> = characterSuccessState.map {
        it.character.name
    }

    override val gender: Flow<String?> = characterSuccessState.map {
        it.character.gender
    }

    override val bloodType: Flow<String?> = characterSuccessState.map {
        it.character.bloodType
    }

    override val birthDate: Flow<Character.BirthDate?> = characterSuccessState.map {
        it.character.birthDate
    }

    override val description: Flow<String?> = characterSuccessState.map {
        it.character.description
    }

    override val translatedDescription: MutableStateFlow<String?> = MutableStateFlow(null)

    override val isFavorite: Flow<Boolean> = characterSuccessState.map {
        it.character.isFavorite
    }

    override val isFavoriteBlocked: Flow<Boolean> = characterSuccessState.map {
        it.character.isFavoriteBlocked
    }

    init {
        characterRepository.load(initialChar.id)
    }

    @Composable
    override fun render() {
        onRender {
            CharacterDialog(this)
        }
    }

    override fun dismiss() {
        onDismiss()
    }

    override fun descriptionTranslation(text: String?) {
        translatedDescription.update { text }
    }

    override fun retry() {

    }

    override fun toggleFavorite() {
        launchIO {
            val charId = initialChar.id
            val mutation = FavoriteToggleMutation(
                characterId = Optional.present(charId)
            )

            aniListClient.mutation(mutation).execute()
        }
    }
}