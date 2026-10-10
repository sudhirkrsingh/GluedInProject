package com.example.gluedinproject.domain.model

data class Content(val id: String, val title: String, val description: String, val thumbnailUrl: String, val bannerUrl: String, val videoUrl: String, val genre: String, val duration: String)
data class ContentRail(val id: String, val title: String, val items: List<Content>)
data class HomeContent(val hero: Content?, val rails: List<ContentRail>)
enum class PlayerEventType { READY, STARTED, PAUSED, SEEKED, BUFFERING_STARTED, BUFFERING_ENDED, COMPLETED, ERROR }
data class PlayerEvent(val contentId: String, val type: PlayerEventType, val positionMs: Long, val detail: String? = null)
