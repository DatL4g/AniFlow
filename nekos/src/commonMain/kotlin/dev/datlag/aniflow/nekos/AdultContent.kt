package dev.datlag.aniflow.nekos

internal data object AdultContent {

    sealed interface Tag : CharSequence {
        val tag: String

        override val length: Int
            get() = tag.length

        override operator fun get(index: Int): Char {
            return tag[index]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return tag.subSequence(startIndex, endIndex)
        }

        data object Bikini : Tag {
            override val tag: String = "Bikini"
        }

        companion object {
            val all = listOf(Bikini)
            val allTags = all.map { it.tag }

            fun exists(tag: String): Boolean {
                return all.any { t ->
                    t.tag.equals(tag, ignoreCase = true)
                }
            }
        }
    }
}