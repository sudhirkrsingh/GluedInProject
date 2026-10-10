package com.example.gluedinproject.data.dto

data class ContentDto(val id: String, val title: String, val description: String, val thumbnailUrl: String, val bannerUrl: String, val videoUrl: String, val genre: String, val duration: String)
data class RailDto(val id: String, val title: String, val contentIds: List<String>)
data class HomeDto(val heroId: String?, val content: List<ContentDto>, val rails: List<RailDto>)
