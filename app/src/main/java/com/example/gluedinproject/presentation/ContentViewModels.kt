package com.example.gluedinproject.presentation
import androidx.lifecycle.*
import com.example.gluedinproject.domain.model.*
import com.example.gluedinproject.domain.usecase.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data object Empty : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
class HomeViewModel(private val getHome: GetHomeContentUseCase) : ViewModel() {
    private val mutable = MutableStateFlow<UiState<HomeContent>>(UiState.Loading)
    val state = mutable.asStateFlow()
    private var job: kotlinx.coroutines.Job? = null
    init { load() }
    fun load() { if (job?.isActive == true) return; mutable.value = UiState.Loading; job = viewModelScope.launch {
        try { val data = getHome(); mutable.value = if (data.hero == null && data.rails.all { it.items.isEmpty() }) UiState.Empty else UiState.Success(data) }
        catch (e: CancellationException) { throw e } catch (e: Exception) { mutable.value = UiState.Error("Could not load the catalogue. Please try again.") }
    } }
}
class ContentViewModel(private val getContent: GetContentUseCase, private val id: String) : ViewModel() {
    private val mutable = MutableStateFlow<UiState<Content>>(UiState.Loading)
    val state = mutable.asStateFlow()
    private var job: kotlinx.coroutines.Job? = null
    init { load() }
    fun load() { if (job?.isActive == true) return; mutable.value = UiState.Loading; job = viewModelScope.launch {
        try { mutable.value = getContent(id)?.let { UiState.Success(it) } ?: UiState.Empty }
        catch (e: CancellationException) { throw e } catch (e: Exception) { mutable.value = UiState.Error("Could not load this title. Please try again.") }
    } }
}
class SimpleFactory<T : ViewModel>(private val create: () -> T) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST") override fun <V : ViewModel> create(modelClass: Class<V>): V = create() as V
}
