package dev.datlag.aniflow.settings

import androidx.datastore.core.DataStore
import dev.datlag.aniflow.settings.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreAppSettings(
    private val dateStore: DataStore<AppSettings>
) : Settings.PlatformAppSettings {
    override val adultContent: Flow<Boolean> = dateStore.data.map { it.adultContent }

    override suspend fun setAdultContent(value: Boolean) {
        dateStore.updateData {
            it.copy(
                adultContent = value
            )
        }
    }
}