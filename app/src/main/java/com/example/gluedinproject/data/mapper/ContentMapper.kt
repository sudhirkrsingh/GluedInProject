package com.example.gluedinproject.data.mapper
import com.example.gluedinproject.data.dto.*
import com.example.gluedinproject.domain.model.*
fun ContentDto.toDomain() = Content(id, title, description, thumbnailUrl, bannerUrl, videoUrl, genre, duration)
fun HomeDto.toDomain(): HomeContent {
    val byId = content.associate { it.id to it.toDomain() }
    return HomeContent(heroId?.let(byId::get), rails.map { rail -> ContentRail(rail.id, rail.title, rail.contentIds.mapNotNull(byId::get)) })
}
