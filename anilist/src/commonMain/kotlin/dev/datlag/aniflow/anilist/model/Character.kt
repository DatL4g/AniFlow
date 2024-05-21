package dev.datlag.aniflow.anilist.model

import dev.datlag.aniflow.anilist.*
import dev.datlag.aniflow.anilist.PageMediaQuery
import kotlinx.serialization.Serializable

@Serializable
data class Character(
    /**
     * The id of the character
     */
    val id: Int,

    /**
     * The names of the character
     */
    val name: Name,

    /**
     * Character images
     */
    val image: Image,

    /**
     * The character's gender.
     * Usually Male, Female, or Non-binary but can be any string.
     */
    val gender: String?,

    /**
     * The characters blood type
     */
    val bloodType: String?,

    /**
     * The character's birthdate
     */
    val birthDate: BirthDate?,

    /**
     * A general description of the character
     */
    val description: String?,

    /**
     * If the character is marked as favourite by the currently authenticated user
     */
    val isFavorite: Boolean,

    /**
     * If the character is blocked from being added to favourites
     */
    val isFavoriteBlocked: Boolean
) {

    @Serializable
    data class Name(
        /**
         * The character's given name
         */
        val first: String?,

        /**
         * The character's middle name
         */
        val middle: String?,

        /**
         * The character's surname
         */
        val last: String?,

        /**
         * The character's first and last name
         */
        val full: String?,

        /**
         * The character's full name in their native language
         */
        val native: String?,

        /**
         * The currently authenticated users preferred name language. Default romaji for
         * non-authenticated
         */
        val userPreferred: String?
    ) {
        constructor(name: MediumQuery.Name) : this(
            first = name.first?.ifBlank { null },
            middle = name.middle?.ifBlank { null },
            last = name.last?.ifBlank { null },
            full = name.full?.ifBlank { null },
            native = name.native?.ifBlank { null },
            userPreferred = name.userPreferred?.ifBlank { null }
        )

        constructor(name: CharacterQuery.Name) : this(
            first = name.first?.ifBlank { null },
            middle = name.middle?.ifBlank { null },
            last = name.last?.ifBlank { null },
            full = name.full?.ifBlank { null },
            native = name.native?.ifBlank { null },
            userPreferred = name.userPreferred?.ifBlank { null }
        )

        constructor(name: PageMediaQuery.Name) : this(
            first = name.first?.ifBlank { null },
            middle = name.middle?.ifBlank { null },
            last = name.last?.ifBlank { null },
            full = name.full?.ifBlank { null },
            native = name.native?.ifBlank { null },
            userPreferred = name.userPreferred?.ifBlank { null }
        )

        constructor(name: AiringQuery.Name) : this(
            first = name.first?.ifBlank { null },
            middle = name.middle?.ifBlank { null },
            last = name.last?.ifBlank { null },
            full = name.full?.ifBlank { null },
            native = name.native?.ifBlank { null },
            userPreferred = name.userPreferred?.ifBlank { null }
        )

        constructor(name: ListQuery.Name) : this(
            first = name.first?.ifBlank { null },
            middle = name.middle?.ifBlank { null },
            last = name.last?.ifBlank { null },
            full = name.full?.ifBlank { null },
            native = name.native?.ifBlank { null },
            userPreferred = name.userPreferred?.ifBlank { null }
        )

        constructor(name: SearchQuery.Name) : this(
            first = name.first?.ifBlank { null },
            middle = name.middle?.ifBlank { null },
            last = name.last?.ifBlank { null },
            full = name.full?.ifBlank { null },
            native = name.native?.ifBlank { null },
            userPreferred = name.userPreferred?.ifBlank { null }
        )

        constructor(name: RecommendationQuery.Name) : this(
            first = name.first?.ifBlank { null },
            middle = name.middle?.ifBlank { null },
            last = name.last?.ifBlank { null },
            full = name.full?.ifBlank { null },
            native = name.native?.ifBlank { null },
            userPreferred = name.userPreferred?.ifBlank { null }
        )
    }

    @Serializable
    data class Image(
        val large: String?,
        val medium: String?
    ) {
        constructor(image: MediumQuery.Image) : this(
            large = image.large?.ifBlank { null },
            medium = image.medium?.ifBlank { null }
        )

        constructor(image: CharacterQuery.Image) : this(
            large = image.large?.ifBlank { null },
            medium = image.medium?.ifBlank { null },
        )

        constructor(image: PageMediaQuery.Image) : this(
            large = image.large?.ifBlank { null },
            medium = image.medium?.ifBlank { null },
        )

        constructor(image: AiringQuery.Image) : this(
            large = image.large?.ifBlank { null },
            medium = image.medium?.ifBlank { null }
        )

        constructor(image: ListQuery.Image) : this(
            large = image.large?.ifBlank { null },
            medium = image.medium?.ifBlank { null },
        )

        constructor(image: SearchQuery.Image) : this(
            large = image.large?.ifBlank { null },
            medium = image.medium?.ifBlank { null },
        )

        constructor(image: RecommendationQuery.Image) : this(
            large = image.large?.ifBlank { null },
            medium = image.medium?.ifBlank { null },
        )
    }

    @Serializable
    data class BirthDate(
        val day: Int?,
        val month: Int?,
        val year: Int?
    ) {
        fun format(): String {
            return buildString {
                if (day != null) {
                    if (day <= 9) {
                        append("0$day")
                    } else {
                        append(day)
                    }
                    append(". ")
                }
                if (month != null) {
                    when (month) {
                        1 -> append("Jan")
                        2 -> append("Feb")
                        3 -> append("Mar")
                        4 -> append("Apr")
                        5 -> append("May")
                        6 -> append("Jun")
                        7 -> append("Jul")
                        8 -> append("Aug")
                        9 -> append("Sep")
                        10 -> append("Oct")
                        11 -> append("Nov")
                        12 -> append("Dec")
                        else -> if (month <= 9) {
                            append("0$month.")
                        } else {
                            append("$month.")
                        }
                    }
                    append(' ')
                }
                if (year != null) {
                    append(year)
                }
            }.trim()
        }

        companion object {
            operator fun invoke(birth: CharacterQuery.DateOfBirth) : BirthDate? {
                if (birth.day == null && birth.month == null && birth.year == null) {
                    return null
                }

                return BirthDate(
                    day = birth.day,
                    month = birth.month,
                    year = birth.year
                )
            }

            operator fun invoke(birth: PageMediaQuery.DateOfBirth): BirthDate? {
                if (birth.day == null && birth.month == null && birth.year == null) {
                    return null
                }

                return BirthDate(
                    day = birth.day,
                    month = birth.month,
                    year = birth.year
                )
            }

            operator fun invoke(birth: MediumQuery.DateOfBirth): BirthDate? {
                if (birth.day == null && birth.month == null && birth.year == null) {
                    return null
                }

                return BirthDate(
                    day = birth.day,
                    month = birth.month,
                    year = birth.year
                )
            }

            operator fun invoke(birth: AiringQuery.DateOfBirth): BirthDate? {
                if (birth.day == null && birth.month == null && birth.year == null) {
                    return null
                }

                return BirthDate(
                    day = birth.day,
                    month = birth.month,
                    year = birth.year
                )
            }

            operator fun invoke(birth: ListQuery.DateOfBirth): BirthDate? {
                if (birth.day == null && birth.month == null && birth.year == null) {
                    return null
                }

                return BirthDate(
                    day = birth.day,
                    month = birth.month,
                    year = birth.year
                )
            }

            operator fun invoke(birth: SearchQuery.DateOfBirth): BirthDate? {
                if (birth.day == null && birth.month == null && birth.year == null) {
                    return null
                }

                return BirthDate(
                    day = birth.day,
                    month = birth.month,
                    year = birth.year
                )
            }

            operator fun invoke(birth: RecommendationQuery.DateOfBirth): BirthDate? {
                if (birth.day == null && birth.month == null && birth.year == null) {
                    return null
                }

                return BirthDate(
                    day = birth.day,
                    month = birth.month,
                    year = birth.year
                )
            }
        }
    }

    companion object {
        operator fun invoke(character: MediumQuery.Node) : Character? {
            val name = character.name?.let(::Name) ?: return null
            val image = character.image?.let(::Image) ?: return null

            return Character(
                id = character.id,
                name = name,
                image = image,
                gender = character.gender?.ifBlank { null },
                bloodType = character.bloodType?.ifBlank { null },
                birthDate = character.dateOfBirth?.let { BirthDate(it) },
                description = character.description?.ifBlank { null },
                isFavorite = character.isFavourite,
                isFavoriteBlocked = character.isFavouriteBlocked
            )
        }

        operator fun invoke(character: PageMediaQuery.Node) : Character? {
            val name = character.name?.let(::Name) ?: return null
            val image = character.image?.let(::Image) ?: return null

            return Character(
                id = character.id,
                name = name,
                image = image,
                gender = character.gender?.ifBlank { null },
                bloodType = character.bloodType?.ifBlank { null },
                birthDate = character.dateOfBirth?.let { BirthDate(it) },
                description = character.description?.ifBlank { null },
                isFavorite = character.isFavourite,
                isFavoriteBlocked = character.isFavouriteBlocked,
            )
        }

        operator fun invoke(character: CharacterQuery.Character) : Character? {
            val name = character.name?.let(::Name) ?: return null
            val image = character.image?.let(::Image) ?: return null

            return Character(
                id = character.id,
                name = name,
                image = image,
                gender = character.gender?.ifBlank { null },
                bloodType = character.bloodType?.ifBlank { null },
                birthDate = character.dateOfBirth?.let { BirthDate(it) },
                description = character.description?.ifBlank { null },
                isFavorite = character.isFavourite,
                isFavoriteBlocked = character.isFavouriteBlocked
            )
        }

        operator fun invoke(character: AiringQuery.Node) : Character? {
            val name = character.name?.let(::Name) ?: return null
            val image = character.image?.let(::Image) ?: return null

            return Character(
                id = character.id,
                name = name,
                image = image,
                gender = character.gender?.ifBlank { null },
                bloodType = character.bloodType?.ifBlank { null },
                birthDate = character.dateOfBirth?.let { BirthDate(it) },
                description = character.description?.ifBlank { null },
                isFavorite = character.isFavourite,
                isFavoriteBlocked = character.isFavouriteBlocked
            )
        }

        operator fun invoke(character: ListQuery.Node) : Character? {
            val name = character.name?.let(::Name) ?: return null
            val image = character.image?.let(::Image) ?: return null

            return Character(
                id = character.id,
                name = name,
                image = image,
                gender = character.gender?.ifBlank { null },
                bloodType = character.bloodType?.ifBlank { null },
                birthDate = character.dateOfBirth?.let { BirthDate(it) },
                description = character.description?.ifBlank { null },
                isFavorite = character.isFavourite,
                isFavoriteBlocked = character.isFavouriteBlocked
            )
        }

        operator fun invoke(character: SearchQuery.Node) : Character? {
            val name = character.name?.let(::Name) ?: return null
            val image = character.image?.let(::Image) ?: return null

            return Character(
                id = character.id,
                name = name,
                image = image,
                gender = character.gender?.ifBlank { null },
                bloodType = character.bloodType?.ifBlank { null },
                birthDate = character.dateOfBirth?.let { BirthDate(it) },
                description = character.description?.ifBlank { null },
                isFavorite = character.isFavourite,
                isFavoriteBlocked = character.isFavouriteBlocked
            )
        }

        operator fun invoke(character: RecommendationQuery.Node) : Character? {
            val name = character.name?.let(::Name) ?: return null
            val image = character.image?.let(::Image) ?: return null

            return Character(
                id = character.id,
                name = name,
                image = image,
                gender = character.gender?.ifBlank { null },
                bloodType = character.bloodType?.ifBlank { null },
                birthDate = character.dateOfBirth?.let { BirthDate(it) },
                description = character.description?.ifBlank { null },
                isFavorite = character.isFavourite,
                isFavoriteBlocked = character.isFavouriteBlocked
            )
        }
    }
}