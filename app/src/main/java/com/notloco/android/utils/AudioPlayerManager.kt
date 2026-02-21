package com.notloco.android.utils

import android.content.Context
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

    private val _playingUrl = MutableStateFlow<String?>(null)
    val playingUrl: StateFlow<String?> = _playingUrl.asStateFlow()

    @OptIn(UnstableApi::class)
    fun playOrToggle(context: Context, url: String) {
        val exo = player ?: ExoPlayer.Builder(context.applicationContext).build().also {
            player = it
            it.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        _playingUrl.value = null
                        currentUrl = null
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (!isPlaying && player?.playbackState != Player.STATE_BUFFERING) {
                        _playingUrl.value = null
                    }
                }
            })
        }

        if (currentUrl == url && exo.isPlaying) {
            exo.pause()
            _playingUrl.value = null
            return
        }

        if (currentUrl == url && !exo.isPlaying) {
            exo.play()
            _playingUrl.value = url
            return
        }

        exo.stop()
        exo.setMediaItem(MediaItem.fromUri(url))
        exo.prepare()
        exo.play()
        currentUrl = url
        _playingUrl.value = url
    }

    fun stop() {
        player?.stop()
        _playingUrl.value = null
        currentUrl = null
    }

    fun release() {
        player?.release()
        player = null
        _playingUrl.value = null
        currentUrl = null
    }
}
