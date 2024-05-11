package dev.datlag.aniflow.other

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

data object UpdateManager {

    fun checkForUpdates(context: Context, onUpdateAvailable: (AppUpdateManager, AppUpdateInfo, updateType: Int) -> Unit) {
        val manager = AppUpdateManagerFactory.create(context)

        manager.appUpdateInfo.addOnSuccessListener { info ->
            val updateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

            if (updateAvailable) {
                val immediate = info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

                if (immediate) {
                    onUpdateAvailable(manager, info, AppUpdateType.IMMEDIATE)
                }
            }
        }
    }

    fun checkResume(context: Context, onUpdateResume: (AppUpdateManager, AppUpdateInfo) -> Unit) {
        val manager = AppUpdateManagerFactory.create(context)

        manager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                onUpdateResume(manager, info)
            }
        }
    }
}