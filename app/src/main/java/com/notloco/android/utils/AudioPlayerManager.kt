package com.notloco.android.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AudioPlayerManager {

    private var player: ExoPlayer? = null
    private var currentUrl: String? = null
    private val handler = Handler(Looper.getMainLooper())

    private val _playingUrl = MutableStateFlow<String?>(null)
    val playingUrl: StateFlow<String?> = _playingUrl.asStateFlow()

    /** Playback progress 0f..1f for the currently playing track */
    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private val progressRunnable = object : Runnable {
        override fun run() {
            val exo = player
            if (exo != null && exo.isPlaying && exo.duration > 0) {
                _playbackProgress.value = exo.currentPosition.toFloat() / exo.duration.toFloat()
                handler.postDelayed(this, 50) // update ~20fps
            }
        }
    }

    private fun startProgressUpdates() {
        handler.removeCallbacks(progressRunnable)
        handler.post(progressRunnable)
    }

    private fun stopProgressUpdates() {
        handler.removeCallbacks(progressRunnable)
    }

    @OptIn(UnstableApi::class)
    fun playOrToggle(context: Context, url: String) {
        val exo = player ?: ExoPlayer.Builder(context.applicationContext).build().also {
            player = it
            it.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        _playingUrl.value = null
                        _playbackProgress.value = 0f
                        currentUrl = null
                        stopProgressUpdates()
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        startProgressUpdates()
                    } else {
                        stopProgressUpdates()
                        if (player?.playbackState != Player.STATE_BUFFERING) {
                            _playingUrl.value = null
                        }
                    }
                }
            })
        }

        if (currentUrl == url && exo.isPlaying) {
            exo.pause()
            _playingUrl.value = null
            stopProgressUpdates()
            return
        }

        if (currentUrl == url && !exo.isPlaying) {
            exo.play()
            _playingUrl.value = url
            startProgressUpdates()
            return
        }

        exo.stop()
        _playbackProgress.value = 0f
        exo.setMediaItem(MediaItem.fromUri(url))
        exo.prepare()
        exo.play()
        currentUrl = url
        _playingUrl.value = url
    }

    fun stop() {
        player?.stop()
        _playingUrl.value = null
        _playbackProgress.value = 0f
        currentUrl = null
        stopProgressUpdates()
    }

    fun release() {
        player?.release()
        player = null
        _playingUrl.value = null
        _playbackProgress.value = 0f
        currentUrl = null
        stopProgressUpdates()
    }
}
