package com.apiguave.tinderclonecompose.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apiguave.tinderclonecompose.domain.message.MessageRepository
import com.apiguave.tinderclonecompose.domain.match.entity.Match
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(private val messageRepository: MessageRepository): ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatViewUiState(
            false,
            null)
    )
    val uiState = _uiState.asStateFlow()

    private val _match = MutableStateFlow<Match?>(null)
    val match = _match.asStateFlow()

    suspend fun getMatchById(matchId: String) = messageRepository.getMatchById(matchId)

    fun setMatch(match: Match){
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        _match.value = match
        _uiState.update { it.copy(isLoading = false) }
    }

    fun getMessages(matchId: String) = messageRepository.getMessages(matchId)

    fun sendMessage(text: String){
        val matchId = _match.value?.id ?: return
        viewModelScope.launch {
            try {
                println("Sending message... $text")
                messageRepository.sendMessage(matchId, text)
                println("Message sent")
            } catch (e: Exception){
                //Delete the message from the displayed list
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun likeMessage(messageId: String){
        val matchId = _match.value?.id ?: return
        viewModelScope.launch {
            try {
                messageRepository.likeMessage(matchId, messageId)
            } catch (e: Exception){
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun unLikeMessage(messageId: String){
        val matchId = _match.value?.id ?: return
        viewModelScope.launch {
            try {
                messageRepository.unLikeMessage(matchId, messageId)
            } catch (e: Exception){
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}

data class ChatViewUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null)