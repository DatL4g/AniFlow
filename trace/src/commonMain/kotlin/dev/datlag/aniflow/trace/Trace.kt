package dev.datlag.aniflow.trace

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import dev.datlag.aniflow.trace.model.SearchResponse
import io.ktor.client.request.forms.*
import io.ktor.utils.io.*

interface Trace {

    @POST("search")
    suspend fun search(
        @Body image: ByteArray,
        @Query("cutBorders") cutBorders: Boolean? = true
    ): SearchResponse
}