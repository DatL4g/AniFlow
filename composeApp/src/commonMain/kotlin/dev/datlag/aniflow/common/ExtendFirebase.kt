package dev.datlag.aniflow.common

import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.model.name
import dev.datlag.aniflow.other.BurningSeriesResolver
import dev.datlag.aniflow.ui.navigation.Component

fun FirebaseFactory.Crashlytics.screen(value: Component) {
    this.customKey("Screen", value::class.name)
}

fun FirebaseFactory.Crashlytics.bs(available: Boolean, versionCode: Int, versionName: String?) {
    this.customKey("BS Availability", available)
    if (available) {
        this.customKey("BS Version Code", versionCode)
        this.customKey("BS Version Name", versionName.toString())
    }
}

fun FirebaseFactory.Crashlytics.bs(resolver: BurningSeriesResolver) = bs(
    available = resolver.isAvailable,
    versionCode = resolver.versionCode,
    versionName = resolver.versionName
)