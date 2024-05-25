package dev.datlag.aniflow.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.Recommend
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.state.DiscoverListType
import dev.icerock.moko.resources.StringResource

fun DiscoverListType.icon(): ImageVector {
    return when (this) {
        is DiscoverListType.Recommendation -> Icons.Rounded.Recommend
        is DiscoverListType.Spring -> Icons.Rounded.LocalFlorist
        is DiscoverListType.Summer -> Icons.Rounded.WbSunny
        is DiscoverListType.Fall -> Icons.Rounded.Thunderstorm
        is DiscoverListType.Winter -> Icons.Rounded.AcUnit
    }
}

fun DiscoverListType.title(): StringResource {
    return when (this) {
        is DiscoverListType.Recommendation -> SharedRes.strings.recommendation
        is DiscoverListType.Spring -> SharedRes.strings.spring
        is DiscoverListType.Summer -> SharedRes.strings.summer
        is DiscoverListType.Fall -> SharedRes.strings.fall
        is DiscoverListType.Winter -> SharedRes.strings.winter
    }
}