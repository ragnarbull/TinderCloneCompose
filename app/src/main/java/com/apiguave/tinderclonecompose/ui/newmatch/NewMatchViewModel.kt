package com.apiguave.tinderclonecompose.ui.newmatch

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apiguave.tinderclonecompose.domain.message.MessageRepository
import com.apiguave.tinderclonecompose.domain.profilecard.entity.NewMatch
import com.giphy.sdk.core.models.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewMatchViewModel(private val messageRepository: MessageRepository): ViewModel() {
    val TAG = "NewMatchViewModel"
    private val _match: MutableStateFlow<NewMatch?> = MutableStateFlow(null)
    val match = _match.asStateFlow()

    fun sendMessage(text: String){
        val matchId = _match.value?.id ?: return
        viewModelScope.launch {
            try {
                messageRepository.sendMessage(matchId, text)
            } catch (e: Exception){
                //Show the message as unsent?
            }
        }
    }

    @SuppressLint("LogNotTimber")
    fun onGifSelected(media: Media) {
        val matchId = _match.value?.id ?: return
        val giphyMediaId = media.id
        Log.d(TAG, "onGifSelected - giphyMediaId: $giphyMediaId")

        viewModelScope.launch {
            try {
                messageRepository.sendGiphyGif(matchId, giphyMediaId)
            } catch (e: Exception){
                Log.d(TAG, "Failed to send the GIF ${e.message}")
            }
        }
    }

    fun setMatch(match: NewMatch){
        Log.d("NewMatchViewModel", "match: $match")
        _match.value = match
    }
}