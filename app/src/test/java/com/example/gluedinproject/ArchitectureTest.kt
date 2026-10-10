package com.example.gluedinproject

import com.example.gluedinproject.data.dto.*
import com.example.gluedinproject.data.mapper.toDomain
import com.example.gluedinproject.data.remote.ContentRemoteDataSource
import com.example.gluedinproject.data.repository.DefaultContentRepository
import com.example.gluedinproject.domain.model.*
import com.example.gluedinproject.domain.repository.*
import com.example.gluedinproject.domain.usecase.*
import com.example.gluedinproject.presentation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ArchitectureTest {
    private val dispatcher = StandardTestDispatcher()
    private val dto = ContentDto("film", "Film", "Description", "thumb", "banner", "video", "Adventure", "10 min")
    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }
    @Test fun mappingResolvesHeroAndRailReferences() {
        val home = HomeDto("film", listOf(dto), listOf(RailDto("rail", "Trending", listOf("film", "missing")))).toDomain()
        assertEquals("film", home.hero?.id)
        assertEquals(listOf(dto.toDomain()), home.rails.single().items)
    }
    @Test fun repositoryFindsSelectedTitleAndReturnsNullForUnknownId() = runTest(dispatcher) {
        val repository = DefaultContentRepository(object : ContentRemoteDataSource {
            override suspend fun fetchHome() = HomeDto("film", listOf(dto), emptyList())
        })
        assertEquals("video", GetContentUseCase(repository)("film")?.videoUrl)
        assertNull(GetContentUseCase(repository)("unknown"))
    }
    private fun repository(home: HomeContent? = null) = object : ContentRepository {
        override suspend fun getHome() = home ?: error("Unavailable")
        override suspend fun getContent(id: String): Content? = null
    }
    @Test fun homeStateTransitionsFromLoadingToSuccess() = runTest(dispatcher) {
        val model = HomeViewModel(GetHomeContentUseCase(repository(HomeContent(dto.toDomain(), emptyList()))))
        assertEquals(UiState.Loading, model.state.value)
        advanceUntilIdle()
        assertTrue(model.state.value is UiState.Success)
    }
    @Test fun emptyCatalogueProducesEmptyState() = runTest(dispatcher) {
        val model = HomeViewModel(GetHomeContentUseCase(repository(HomeContent(null, emptyList()))))
        advanceUntilIdle(); assertEquals(UiState.Empty, model.state.value)
    }
    @Test fun dataFailureProducesRecoverableError() = runTest(dispatcher) {
        val model = HomeViewModel(GetHomeContentUseCase(repository()))
        advanceUntilIdle(); assertTrue(model.state.value is UiState.Error)
        model.load(); assertEquals(UiState.Loading, model.state.value)
        advanceUntilIdle(); assertTrue(model.state.value is UiState.Error)
    }
    @Test fun unknownTitleProducesEmptyDetailState() = runTest(dispatcher) {
        val model = ContentViewModel(GetContentUseCase(repository()), "missing")
        advanceUntilIdle(); assertEquals(UiState.Empty, model.state.value)
    }
    @Test fun eventUseCasePreservesContentAndPosition() = runTest(dispatcher) {
        val events = mutableListOf<PlayerEvent>()
        val useCase = RecordPlayerEventUseCase(EventRepository { events += it })
        val event = PlayerEvent("film", PlayerEventType.SEEKED, 42000)
        useCase(event); assertEquals(listOf(event), events)
    }
}
