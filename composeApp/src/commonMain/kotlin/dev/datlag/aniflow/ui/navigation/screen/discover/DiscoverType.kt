package dev.datlag.aniflow.ui.navigation.screen.discover

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import dev.datlag.aniflow.SharedRes
import dev.icerock.moko.resources.StringResource

sealed interface DiscoverType {
    val icon: ImageVector
    val text: StringResource

    data object Top100 : DiscoverType {
        override val icon: ImageVector = Icons.Rounded.Star
        override val text: StringResource = SharedRes.strings.top_100
    }

    data object TopMovies : DiscoverType {
        override val icon: ImageVector = Icons.Rounded.Movie
        override val text: StringResource = SharedRes.strings.top_movies
    }

    data object Spring : DiscoverType {
        override val icon: ImageVector = Icons.Rounded.LocalFlorist
        override val text: StringResource = SharedRes.strings.spring
    }

    data object Summer : DiscoverType {
        override val icon: ImageVector = Icons.Rounded.WbSunny
        override val text: StringResource = SharedRes.strings.summer
    }

    data object Fall : DiscoverType {
        override val icon: ImageVector = Icons.Rounded.Thunderstorm
        override val text: StringResource = SharedRes.strings.fall
    }

    data object Winter : DiscoverType {
        override val icon: ImageVector = Icons.Rounded.AcUnit
        override val text: StringResource = SharedRes.strings.winter
    }
}