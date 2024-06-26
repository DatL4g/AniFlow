package dev.datlag.aniflow.nekos.model

import dev.datlag.aniflow.model.serializer.SerializableImmutableList
import dev.datlag.aniflow.nekos.AdultContent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ImagesResponse(
    @SerialName("items") val items: SerializableImmutableList<Item>,
    @SerialName("count") val count: Int = items.size,
) {
    @Transient
    val isError: Boolean = items.isEmpty()

    @Serializable
    data class Item(
        @SerialName("id") val id: Int,
        @SerialName("image_url") val imageUrl: String? = null,
        @SerialName("sample_url") val sampleUrl: String? = null,
        @SerialName("characters") val characters: SerializableImmutableList<Character> = persistentListOf(),
        @SerialName("tags") val tags: SerializableImmutableList<Tag> = persistentListOf(),
    ) {
        @Transient
        val hasAdultTag: Boolean = tags.any {
            it.isNsfw || AdultContent.Tag.exists(it.name)
        }

        @Serializable
        data class Character(
            @SerialName("id") val id: Int,
            @SerialName("name") val name: String,
            @SerialName("description") val description: String? = null,
            @SerialName("gender") val gender: String? = null,
        )

        @Serializable
        data class Tag(
            @SerialName("id") val id: Int,
            @SerialName("name") val name: String,
            @SerialName("is_nsfw") val isNsfw: Boolean = false,
        )
    }
}
