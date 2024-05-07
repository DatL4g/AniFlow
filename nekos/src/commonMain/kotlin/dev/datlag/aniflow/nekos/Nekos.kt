package dev.datlag.aniflow.nekos

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import dev.datlag.aniflow.nekos.model.ImagesResponse

interface Nekos {

    @GET("images")
    suspend fun images(
        @Query("rating") rating: String,
        @Query("offset") offset: Int? = null,
    ): ImagesResponse
}