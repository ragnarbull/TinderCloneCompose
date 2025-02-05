package com.apiguave.tinderclonecompose.extensions.giphyvideoplayer

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.media3.common.util.UnstableApi
import com.apiguave.tinderclonecompose.databinding.VideoControlsViewBinding
import com.giphy.sdk.ui.R
import kotlinx.coroutines.*
import timber.log.Timber

@UnstableApi class VideoControls @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DOUBLE_TOUCH_INTERVAL = 250L
        private const val PAUSE_TOUCH_INTERVAL = 300L
        private const val HIDE_CONTROLS_DELAY = 2000L
        private const val HIDE_CONTROLS_INITIAL_DELAY = 3000L
        private const val HIDE_CONTROLS_DURATION = 400L
    }

    private var firstStart = false

    private lateinit var player: VideoPlayer
    private lateinit var videoUrl: String

    private var hideControlsAnimation: ViewPropertyAnimatorCompat? = null
    private var hideSeekOverlayAnimation: ViewPropertyAnimatorCompat? = null

    private var lastTouchX = 0f
    private var isDoubleClickPossible = false
    private var clickJob: Job? = null
    private var pauseJob: Job? = null
    private var pause = true

    private val viewBinding: VideoControlsViewBinding =
        VideoControlsViewBinding.bind(inflate(context, R.layout.gph_video_controls_view, this))

    private val listener: PlayerStateListener = { playerState ->
        when (playerState) {
            VideoPlayerState.Idle,
            VideoPlayerState.Buffering,
            VideoPlayerState.Ended -> {
                viewBinding.progressBar.visibility = INVISIBLE
            }
            VideoPlayerState.Playing -> {
                pause = false
                viewBinding.progressBar.visibility = VISIBLE
                if (firstStart) {
                    firstStart = false
                    hideControls(HIDE_CONTROLS_INITIAL_DELAY)
                } else {
                    hideControls()
                }
            }
            is VideoPlayerState.TimelineChanged -> {
                if (playerState.duration > 0) {
                    viewBinding.progressBar.setDuration(playerState.duration)
                }
            }
            is VideoPlayerState.MuteChanged -> {
                updateSoundModeIcon()
            }
            is VideoPlayerState.CaptionsVisibilityChanged -> {
                updateCaptionsIcon(playerState.visible)
            }
            is VideoPlayerState.CaptionsTextChanged -> {
                viewBinding.captionsButton.visibility = VISIBLE
            }
            else -> { }
        }
    }

    init {
        setupTouchListeners()
        viewBinding.soundButton.isClickable = false
        viewBinding.soundButtonOff.isClickable = false
        viewBinding.captionsButton.setOnClickListener {
            if (this::player.isInitialized) {
                player.showCaptions = !player.showCaptions
                showControls(progress = true, sound = true)
            }
        }
    }

    fun prepare(videoUrl: String, player: VideoPlayer) {
        viewBinding.captionsButton.visibility = GONE
        this.videoUrl = videoUrl
        this.player = player
        this.firstStart = true
        updateSoundModeIcon()
        player.addListener(listener)
        updateCaptionsIcon(player.showCaptions)
    }

    private fun resumeVideo() {
        pause = false
        updatePauseState(pause)
        clickJob?.cancel()
        clickJob = null
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListeners() {
        setOnClickListener {
            pauseJob?.cancel()
            pauseJob = null
            if (pause) {
                resumeVideo()
                return@setOnClickListener
            }

            val doubleTouchSize = width / 3
            if (lastTouchX < doubleTouchSize || lastTouchX > width - doubleTouchSize) {
                clickJob = if (isDoubleClickPossible) {
                    clickJob?.cancel()
                    null
                } else {
                    // delay the simple click action until the double click option is excluded
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(DOUBLE_TOUCH_INTERVAL)
                        performOnClick()
                    }
                }
                isDoubleClickPossible = !isDoubleClickPossible
            } else {
                clickJob?.cancel()
                clickJob = null
                isDoubleClickPossible = false
                performOnClick()
            }
        }

        setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = event.x

                    pauseJob = GlobalScope.launch(Dispatchers.Main) {
                        delay(PAUSE_TOUCH_INTERVAL)
                        pause = true
                        updatePauseState(pause)
                    }
                }
                MotionEvent.ACTION_CANCEL -> resumeVideo()
            }
            false
        }
    }

    fun onPause() {
        pause = true
    }

    fun onResume() {
        pause = false
    }

    private fun updatePauseState(pause: Boolean) {
        // TODO:
        // sendAnalytics(if (pause) Events.VIDEO_PAUSE else Events.VIDEO_RESUME)
        if (pause) player.onPause() else player.onResume()
        hideControls(0)
    }

    private fun performOnClick() {
        isDoubleClickPossible = false
        player.setVolume(if (player.getVolume() > 0) 0f else 1.0f)
        showControls(progress = true, sound = true)
    }

    private fun seek(position: Long) {
        player.seekTo(position)
        viewBinding.progressBar.setPosition(player.currentPosition)
        showAndHideSeekOverlay()
    }

    private fun showAndHideSeekOverlay() {
        hideSeekOverlayAnimation?.cancel()
        viewBinding.seekOverlay.visibility = VISIBLE
        viewBinding.seekOverlay.alpha = 1f
        hideSeekOverlayAnimation = ViewCompat.animate(viewBinding.seekOverlay)
            .alpha(0f)
            .withEndAction {
                viewBinding.seekOverlay.visibility = GONE
            }
            .setDuration(250)
            .setStartDelay(1000)
        hideSeekOverlayAnimation?.start()
    }

    private fun showControls(progress: Boolean = false, sound: Boolean = false) {
        Timber.d("showControls")
        hideControlsAnimation?.cancel()
        hideControlsAnimation = null
        viewBinding.controls.alpha = 1f
        viewBinding.controls.visibility = VISIBLE

        viewBinding.soundButton.visibility = if (sound) VISIBLE else GONE
        viewBinding.progressBar.visibility = if (progress) VISIBLE else GONE

        if (player.isPlaying) {
            hideControls()
        }
    }

    private fun hideControls(delay: Long = HIDE_CONTROLS_DELAY) {
        Timber.d("hideControls")
        hideControlsAnimation?.cancel()
        hideControlsAnimation = null

        hideControlsAnimation = ViewCompat.animate(viewBinding.controls)
            .alpha(0f)
            .withEndAction {
                viewBinding.controls.visibility = GONE
            }
            .setDuration(HIDE_CONTROLS_DURATION)
            .setStartDelay(delay)
        hideControlsAnimation?.start()
    }

    fun updateProgress(milliseconds: Long) {
        viewBinding.progressBar.setPosition(milliseconds)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateSoundModeIcon()
    }

    private fun updateSoundModeIcon() {
        if (this::player.isInitialized) {
            viewBinding.soundButton.setImageResource(if (player.getVolume() > 0) R.drawable.gph_ic_sound else R.drawable.gph_ic_no_sound)
            viewBinding.soundButtonOff.visibility =
                if (player.getVolume() == 0f) VISIBLE else GONE
        }
    }

    private fun updateCaptionsIcon(visible: Boolean) {
        viewBinding.captionsButton.setImageResource(if (visible) R.drawable.gph_ic_caption_on else R.drawable.gph_ic_caption_off)
    }
}
