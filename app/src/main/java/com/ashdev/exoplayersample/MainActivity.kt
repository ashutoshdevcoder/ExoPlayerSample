package com.ashdev.exoplayersample

import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.ashdev.exoplayersample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        playerViewModel = PlayerViewModel()
        playerView = binding.playerView
        initializePlayer()
        setupBackgroundAudio()
    }
    private fun initializePlayer() {
        // Create player instance
        if (playerViewModel.player == null) {
            playerViewModel.player = ExoPlayer.Builder(this).build()

            // Create media item
            val mediaItem =
                MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")

            // Set media item and prepare to play
            playerViewModel.player?.setMediaItem(mediaItem)
            playerViewModel.player?.prepare()
            playerViewModel.player?.play()
        }
        else
        {
            playerViewModel.player?.seekTo(playerViewModel.currentPosition)
            if (playerViewModel.isPlaying) {
                playerViewModel.player?.play()
            }
        }
        playerView.player = playerViewModel.player
        playerViewModel.player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    playerViewModel.currentPosition = playerViewModel.player?.currentPosition ?: 0L
                    playerViewModel.isPlaying = playerViewModel.player?.isPlaying ?: false
                }
            }
        })

    }

    private fun setupBackgroundAudio() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        playerViewModel.player?.setAudioAttributes(audioAttributes, true)
        playerViewModel.player?.setHandleAudioBecomingNoisy(true)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Adjust player view for new orientation
        val layoutParams = playerView.layoutParams
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        playerView.layoutParams = layoutParams
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.currentPosition = playerViewModel.player?.currentPosition ?: 0L
        playerViewModel.isPlaying = playerViewModel.player?.isPlaying ?: false
        playerViewModel.player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        playerViewModel.player?.release()
        playerViewModel.player = null
    }
}