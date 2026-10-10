package com.example.gluedinproject.domain.repository
import com.example.gluedinproject.domain.model.*
interface ContentRepository {
    suspend fun getHome(): HomeContent
    suspend fun getContent(id: String): Content?
}
fun interface EventRepository { suspend fun record(event: PlayerEvent) }
