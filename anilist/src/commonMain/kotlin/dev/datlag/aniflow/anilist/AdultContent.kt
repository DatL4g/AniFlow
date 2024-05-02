package dev.datlag.aniflow.anilist

internal data object AdultContent {

    fun isAdultContent(schedule: AiringQuery.AiringSchedule): Boolean {
        return schedule.media?.isAdult == true || schedule.media?.genresFilterNotNull()?.any {
            Genre.exists(it)
        } == true
    }

    sealed interface Genre : CharSequence {
        val tag: String

        override val length: Int
            get() = tag.length

        override operator fun get(index: Int): Char {
            return tag[index]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return tag.subSequence(startIndex, endIndex)
        }

        data object Hentai : Genre {
            override val tag: String = "Hentai"
        }

        data object Ecchi : Genre {
            override val tag: String = "Ecchi"
        }

        companion object {
            val all = listOf(Hentai, Ecchi)
            val allTags = all.map { it.tag }

            fun exists(tag: String): Boolean {
                return all.any { g ->
                    g.tag.equals(tag, ignoreCase = true)
                }
            }
        }
    }
}