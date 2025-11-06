package com.ashdev.exoplayersample

import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer

class PlayerViewModel: ViewModel() {
    var player: ExoPlayer? = null
    var currentPosition: Long = 0L
    var isPlaying: Boolean = false

    override fun onCleared() {
        super.onCleared()
        player?.release()
        player = null
    }
}