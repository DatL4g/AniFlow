package dev.datlag.aniflow.other

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.datlag.aniflow.model.ifValueOrNull

open class BurningSeriesReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { i ->
            val seriesHref = i.getStringExtra("href")?.ifBlank { null } ?: return@let
            val watched = i.getIntExtra("watched", -1).ifValueOrNull(-1) { return@let }

            // ToDo("check connected href and set watched status")
        }
    }
}