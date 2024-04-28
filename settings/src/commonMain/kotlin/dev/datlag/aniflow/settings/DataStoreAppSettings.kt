package dev.datlag.aniflow.settings

import androidx.datastore.core.DataStore
import dev.datlag.aniflow.settings.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreAppSettings(
    private val dateStore: DataStore<AppSettings>
) : Settings.PlatformAppSettings {
    override val adultContent: Flow<Boolean> = dateStore.data.map { it.adultContent }
    override val color: Flow<AppSettings.Color?> = dateStore.data.map { it.color }

    override suspend fun setAdultContent(value: Boolean) {
        dateStore.updateData {
            it.copy(
                adultContent = value
            )
        }
    }

    override suspend fun setColor(value: AppSettings.Color?) {
        dateStore.updateData {
            it.copy(
                color = value
            )
        }
    }

    override suspend fun setData(adultContent: Boolean, color: AppSettings.Color?) {
        dateStore.updateData {
            it.copy(
                adultContent = adultContent,
                color = color
            )
        }
    }
}