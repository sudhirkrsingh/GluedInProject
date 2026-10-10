package com.example.gluedinproject.presentation.player
import androidx.media3.common.*
import com.example.gluedinproject.domain.model.*
class PlayerEventHandler(private val id: String, private val position: () -> Long, private val emit: (PlayerEvent) -> Unit) : Player.Listener {
    private var buffering = false
    private fun event(type: PlayerEventType, detail: String? = null) = emit(PlayerEvent(id, type, position().coerceAtLeast(0), detail))
    override fun onPlaybackStateChanged(state: Int) {
        val nowBuffering = state == Player.STATE_BUFFERING
        if (nowBuffering != buffering) { buffering = nowBuffering; event(if (buffering) PlayerEventType.BUFFERING_STARTED else PlayerEventType.BUFFERING_ENDED) }
        when(state) { Player.STATE_READY -> event(PlayerEventType.READY); Player.STATE_ENDED -> event(PlayerEventType.COMPLETED); Player.STATE_BUFFERING, Player.STATE_IDLE -> Unit }
    }
    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        if (!playWhenReady) event(PlayerEventType.PAUSED)
    }
    override fun onIsPlayingChanged(isPlaying: Boolean) { if(isPlaying) event(PlayerEventType.STARTED) }
    override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) { if(reason == Player.DISCONTINUITY_REASON_SEEK) event(PlayerEventType.SEEKED) }
    override fun onPlayerError(error: PlaybackException) { event(PlayerEventType.ERROR, error.errorCodeName) }
}
