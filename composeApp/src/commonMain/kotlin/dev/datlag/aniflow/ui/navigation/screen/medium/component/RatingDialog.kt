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
import io.github.aakira.napier.Napier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingDialog(
    state: UseCaseState,
    initialValue: Int,
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
        body = RatingBody.Custom(body = { }),
        header = Header.Default(
            icon = IconSource(imageVector = Icons.Rounded.Star),
            title = "Rating"
        )
    )
}