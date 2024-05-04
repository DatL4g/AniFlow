package dev.datlag.aniflow.settings

import androidx.datastore.core.DataStore
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.settings.model.CharLanguage
import dev.datlag.aniflow.settings.model.Color
import dev.datlag.aniflow.settings.model.TitleLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DataStoreAppSettings(
    private val dataStore: DataStore<AppSettings>
) : Settings.PlatformAppSettings {
    override val adultContent: Flow<Boolean> = dataStore.data.map { it.adultContent }.distinctUntilChanged()
    override val color: Flow<Color?> = dataStore.data.map { it.color }.distinctUntilChanged()
    override val titleLanguage: Flow<TitleLanguage?> = dataStore.data.map { it.titleLanguage }.distinctUntilChanged()
    override val charLanguage: Flow<CharLanguage?> = dataStore.data.map { it.charLanguage }.distinctUntilChanged()
    override val viewManga: Flow<Boolean> = dataStore.data.map { it.viewManga }.distinctUntilChanged()

    override suspend fun setAdultContent(value: Boolean) {
        dataStore.updateData {
            it.copy(
                adultContent = value
            )
        }
    }

    override suspend fun setColor(value: Color?) {
        dataStore.updateData {
            it.copy(
                color = value
            )
        }
    }

    override suspend fun setTitleLanguage(value: TitleLanguage?) {
        dataStore.updateData {
            it.copy(
                titleLanguage = value
            )
        }
    }

    override suspend fun setCharLanguage(value: CharLanguage?) {
        dataStore.updateData {
            it.copy(
                charLanguage = value
            )
        }
    }

    override suspend fun setViewManga(value: Boolean) {
        dataStore.updateData {
            it.copy(
                viewManga = value
            )
        }
    }

    override suspend fun setData(
        adultContent: Boolean,
        color: Color?,
        titleLanguage: TitleLanguage?,
        charLanguage: CharLanguage?,
    ) {
        dataStore.updateData {
            it.copy(
                adultContent = adultContent,
                color = color,
                titleLanguage = titleLanguage,
                charLanguage = charLanguage,
            )
        }
    }
}