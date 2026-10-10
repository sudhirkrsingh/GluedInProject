package com.example.gluedinproject.data.repository
import android.util.Log
import com.example.gluedinproject.data.remote.ContentRemoteDataSource
import com.example.gluedinproject.data.mapper.toDomain
import com.example.gluedinproject.domain.model.*
import com.example.gluedinproject.domain.repository.*
class DefaultContentRepository(private val source: ContentRemoteDataSource) : ContentRepository {
    override suspend fun getHome() = source.fetchHome().toDomain()
    override suspend fun getContent(id: String) = source.fetchHome().content.find { it.id == id }?.toDomain()
}
class LoggingEventRepository : EventRepository {
    override suspend fun record(event: PlayerEvent) { Log.i("PlayerAnalytics", "PLAYER_${event.type} content=${event.contentId} position=${event.positionMs} detail=${event.detail.orEmpty()}") }
}
