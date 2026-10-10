package com.example.gluedinproject.presentation.player
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.*
import androidx.media3.exoplayer.ExoPlayer
import com.example.gluedinproject.*
import com.example.gluedinproject.databinding.ActivityPlayerBinding
import com.example.gluedinproject.domain.model.Content
import com.example.gluedinproject.presentation.*
import kotlinx.coroutines.launch
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private var item: Content? = null
    private val contentModel: ContentViewModel by viewModels { SimpleFactory { ContentViewModel((application as OttApplication).getContent, intent.getStringExtra("content_id").orEmpty()) } }
    private val model: PlayerViewModel by viewModels { viewModelFactory { initializer { PlayerViewModel(createSavedStateHandle(), (application as OttApplication).recordEvent) } } }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); binding = ActivityPlayerBinding.inflate(layoutInflater); setContentView(binding.root); applyInsets(binding.root)
        binding.back.setOnClickListener { finish() }
        binding.retry.setOnClickListener {
            model.clearError()
            if (item == null) contentModel.load() else { player?.prepare(); player?.play() }
        }
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { contentModel.state.collect { state ->
                renderState(state, binding.playerView, binding.loading, binding.statePanel, binding.message, binding.retry)
                if (state is UiState.Success) { item = state.data; binding.title.text = state.data.title; initializePlayer() }
            } }
            launch { model.error.collect { error ->
                if(error != null) { binding.statePanel.isVisible = true; binding.message.text = error; binding.retry.isVisible = true; binding.loading.isVisible = false }
                else if(item != null) binding.statePanel.isVisible = false
            } }
        } }
    }
    private fun initializePlayer() {
        val content = item ?: return
        if (player != null || !lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return
        val exo = ExoPlayer.Builder(this).build(); player = exo
        exo.setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(), true)
        exo.setHandleAudioBecomingNoisy(true)
        exo.addListener(PlayerEventHandler(content.id, { exo.currentPosition }, model::onEvent))
        exo.addListener(object : Player.Listener { override fun onIsPlayingChanged(isPlaying: Boolean) { if(isPlaying) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) } })
        binding.playerView.player = exo
        exo.setMediaItem(MediaItem.fromUri(content.videoUrl)); exo.seekTo(model.position); exo.playWhenReady = model.playWhenReady; exo.prepare()
    }
    override fun onStart() { super.onStart(); if (::binding.isInitialized) initializePlayer() }
    override fun onStop() {
        player?.let { exo -> model.position = exo.currentPosition.coerceAtLeast(0); model.playWhenReady = exo.playWhenReady; exo.pause(); binding.playerView.player = null; exo.release() }
        player = null; window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); super.onStop()
    }
}
