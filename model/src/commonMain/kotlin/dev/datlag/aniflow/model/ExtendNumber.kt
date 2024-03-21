package dev.datlag.aniflow.model

import kotlin.math.round

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

fun Float.round(decimals: Int): Float {
    var multiplier = 1F
    repeat(decimals) { multiplier *= 10 }
    return round(this *  multiplier) / multiplier
}