package dev.datlag.aniflow.anilist.model

import dev.datlag.aniflow.anilist.CharacterQuery
import kotlinx.serialization.Serializable

@Serializable
open class Character(
    open val id: Int,
    open val gender: String?,
    open val bloodType: String?,
) {
    constructor(char: CharacterQuery.Character) : this(
        id = char.id,
        gender = char.gender?.ifBlank { null },
        bloodType = char.bloodType?.ifBlank { null },
    )
}
