package com.example.gluedinproject.domain.usecase
import com.example.gluedinproject.domain.repository.*
import com.example.gluedinproject.domain.model.PlayerEvent
class GetHomeContentUseCase(private val repository: ContentRepository) { suspend operator fun invoke() = repository.getHome() }
class GetContentUseCase(private val repository: ContentRepository) { suspend operator fun invoke(id: String) = repository.getContent(id) }
class RecordPlayerEventUseCase(private val repository: EventRepository) { suspend operator fun invoke(event: PlayerEvent) = repository.record(event) }
