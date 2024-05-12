package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeler.sheets.rating.models.RatingBody
import com.maxkeppeler.sheets.rating.models.RatingConfig
import com.maxkeppeler.sheets.rating.models.RatingSelection
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.stringRes
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingDialog(
    state: UseCaseState,
    initialValue: Int,
    type: MediaType,
    onRating: (Int) -> Unit,
) {
    com.maxkeppeler.sheets.rating.RatingDialog(
        state = state,
        config = RatingConfig(
            ratingZeroValid = true,
            ratingOptionsCount = 5,
            ratingOptionsSelected = if (initialValue < 0) null else initialValue
        ),
        selection = RatingSelection(
            onSelectRating = { rating, _ ->
                onRating(rating)
            }
        ),
        body = RatingBody.Default(
            bodyText = stringResource(SharedRes.strings.rating_text, stringResource(type.stringRes()))
        ),
        header = Header.Default(
            icon = IconSource(imageVector = Icons.Rounded.Star),
            title = stringResource(SharedRes.strings.rating)
        )
    )
}