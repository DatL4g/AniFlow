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
    private val dateStore: DataStore<AppSettings>
) : Settings.PlatformAppSettings {
    override val adultContent: Flow<Boolean> = dateStore.data.map { it.adultContent }.distinctUntilChanged()
    override val color: Flow<Color?> = dateStore.data.map { it.color }.distinctUntilChanged()
    override val titleLanguage: Flow<TitleLanguage?> = dateStore.data.map { it.titleLanguage }.distinctUntilChanged()
    override val charLanguage: Flow<CharLanguage?> = dateStore.data.map { it.charLanguage }.distinctUntilChanged()

    override suspend fun setAdultContent(value: Boolean) {
        dateStore.updateData {
            it.copy(
                adultContent = value
            )
        }
    }

    override suspend fun setColor(value: Color?) {
        dateStore.updateData {
            it.copy(
                color = value
            )
        }
    }

    override suspend fun setTitleLanguage(value: TitleLanguage?) {
        dateStore.updateData {
            it.copy(
                titleLanguage = value
            )
        }
    }

    override suspend fun setCharLanguage(value: CharLanguage?) {
        dateStore.updateData {
            it.copy(
                charLanguage = value
            )
        }
    }

    override suspend fun setData(
        adultContent: Boolean,
        color: Color?,
        titleLanguage: TitleLanguage?,
        charLanguage: CharLanguage?,
    ) {
        dateStore.updateData {
            it.copy(
                adultContent = adultContent,
                color = color,
                titleLanguage = titleLanguage,
                charLanguage = charLanguage,
            )
        }
    }
}