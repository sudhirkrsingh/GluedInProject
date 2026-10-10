package com.example.gluedinproject.presentation.player
import androidx.lifecycle.*
import com.example.gluedinproject.domain.model.*
import com.example.gluedinproject.domain.usecase.RecordPlayerEventUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class PlayerViewModel(private val savedState: SavedStateHandle, private val record: RecordPlayerEventUseCase) : ViewModel() {
    var position: Long
        get() = savedState["position"] ?: 0L
        set(value) { savedState["position"] = value }
    var playWhenReady: Boolean
        get() = savedState["playWhenReady"] ?: true
        set(value) { savedState["playWhenReady"] = value }
    private val queue = Channel<PlayerEvent>(Channel.UNLIMITED)
    private val mutableError = MutableStateFlow<String?>(null)
    val error = mutableError.asStateFlow()
    init { viewModelScope.launch { for(event in queue) record(event) } }
    fun onEvent(event: PlayerEvent) {
        if (event.type == PlayerEventType.ERROR) mutableError.value = "Playback failed. Check your connection and try again."
        if (event.type == PlayerEventType.READY) mutableError.value = null
        queue.trySend(event)
    }
    fun clearError() { mutableError.value = null }
    override fun onCleared() { queue.close() }
}
