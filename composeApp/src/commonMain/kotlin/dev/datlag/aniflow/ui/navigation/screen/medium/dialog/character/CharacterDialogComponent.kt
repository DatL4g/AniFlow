package dev.datlag.aniflow.ui.navigation.screen.medium.dialog.character

import androidx.compose.runtime.Composable
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.arkivanov.decompose.ComponentContext
import dev.datlag.aniflow.anilist.CharacterStateMachine
import dev.datlag.aniflow.anilist.FavoriteToggleMutation
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.common.nullableFirebaseInstance
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.model.safeFirstOrNull
import dev.datlag.aniflow.other.Constants
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
    private val aniListFallbackClient by di.instance<ApolloClient>(Constants.AniList.FALLBACK_APOLLO_CLIENT)
    private val characterStateMachine = CharacterStateMachine(
        client = aniListClient,
        fallbackClient = aniListFallbackClient,
        crashlytics = di.nullableFirebaseInstance()?.crashlytics,
        id = initialChar.id
    )

    override val state = characterStateMachine.state.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = characterStateMachine.currentState
    )
    private val characterSuccessState = state.mapNotNull {
        it.safeCast<CharacterStateMachine.State.Success>()
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    private val id = characterSuccessState.mapNotNull {
        it?.character?.id
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialChar.id
    )

    override val image: StateFlow<Character.Image> = characterSuccessState.mapNotNull {
        it?.character?.image
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialChar.image
    )

    override val name: StateFlow<Character.Name> = characterSuccessState.mapNotNull {
        it?.character?.name
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialChar.name
    )

    override val gender: StateFlow<String?> = characterSuccessState.mapNotNull {
        it?.character?.gender
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialChar.gender
    )

    override val bloodType: StateFlow<String?> = characterSuccessState.mapNotNull {
        it?.character?.bloodType
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialChar.bloodType
    )

    override val birthDate: StateFlow<Character.BirthDate?> = characterSuccessState.mapNotNull {
        it?.character?.birthDate
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialChar.birthDate
    )

    override val description: StateFlow<String?> = characterSuccessState.mapNotNull {
        it?.character?.description
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialChar.description
    )

    override val translatedDescription: MutableStateFlow<String?> = MutableStateFlow(null)

    override val isFavorite: StateFlow<Boolean> = characterSuccessState.mapNotNull {
        it?.character?.isFavorite
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialChar.isFavorite
    )

    override val isFavoriteBlocked: StateFlow<Boolean> = characterSuccessState.mapNotNull {
        it?.character?.isFavoriteBlocked
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialChar.isFavoriteBlocked
    )

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
        launchIO {
            characterStateMachine.dispatch(CharacterStateMachine.Action.Retry)
        }
    }

    override fun toggleFavorite() {
        launchIO {
            val charId = id.safeFirstOrNull() ?: id.value
            val mutation = FavoriteToggleMutation(
                characterId = Optional.present(charId)
            )

            aniListClient.mutation(mutation).execute()
        }
    }
}