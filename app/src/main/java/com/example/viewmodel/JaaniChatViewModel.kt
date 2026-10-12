package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ChatMessageEntity
import com.example.data.PreferencesManager
import com.example.data.UserSettings
import com.example.network.GeminiApiClient
import com.example.service.NotificationHelper
import com.example.service.SpeechInputState
import com.example.service.VoiceEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    CHAT,
    CALL
}

class JaaniChatViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val chatDao = db.chatDao()
    private val prefsManager = PreferencesManager(application)
    private val geminiClient = GeminiApiClient()
    val voiceEngine = VoiceEngine(application)

    val messages: StateFlow<List<ChatMessageEntity>> = chatDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _settings = MutableStateFlow(prefsManager.getSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.CHAT)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _lastUserSpokenText = MutableStateFlow("")
    val lastUserSpokenText: StateFlow<String> = _lastUserSpokenText.asStateFlow()

    private val _lastJaaniSpokenText = MutableStateFlow("")
    val lastJaaniSpokenText: StateFlow<String> = _lastJaaniSpokenText.asStateFlow()

    val inputState: StateFlow<SpeechInputState> = voiceEngine.inputState
    val isSpeaking: StateFlow<Boolean> = voiceEngine.isSpeaking

    init {
        NotificationHelper.createNotificationChannel(application)
        voiceEngine.applySettings(_settings.value)
        initializeWelcomeMessageIfNeeded()
    }

    private fun initializeWelcomeMessageIfNeeded() {
        viewModelScope.launch {
            val count = chatDao.getCount()
            if (count == 0) {
                val current = _settings.value
                val welcome = "Hii ${current.userName}! 🥰 Main tumhari ${current.jaaniName} hu. Kaisa raha tumhara din? Mujhe bohot mann kar raha tha tumse baat karne ka! Kuch bhi share karo na ✨"
                chatDao.insertMessage(
                    ChatMessageEntity(
                        text = welcome,
                        isFromUser = false,
                        status = "READ"
                    )
                )
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
        if (screen == AppScreen.CALL) {
            // When opening call screen, Jaani gives a sweet phone greeting
            val current = _settings.value
            val greeting = "Hii ${current.userName}! Haan bolo na babu, main sun rahi hu! 🥰"
            _lastJaaniSpokenText.value = greeting
            if (current.ttsVoiceEnabled) {
                voiceEngine.speak(greeting)
            }
        } else {
            voiceEngine.stopSpeaking()
            voiceEngine.stopListening()
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val trimmed = userText.trim()

        viewModelScope.launch {
            // 1. Insert user message
            chatDao.insertMessage(
                ChatMessageEntity(
                    text = trimmed,
                    isFromUser = true,
                    status = "READ"
                )
            )

            // 2. Set typing state
            _isTyping.value = true

            // 3. Fetch recent history
            val recent = chatDao.getRecentMessages(10).reversed()

            // 4. Call Gemini
            val reply = geminiClient.generateReply(trimmed, recent, _settings.value)

            // 5. Insert reply
            chatDao.insertMessage(
                ChatMessageEntity(
                    text = reply,
                    isFromUser = false,
                    status = "READ"
                )
            )

            _isTyping.value = false

            // If voice call is active or voice is enabled
            if (_currentScreen.value == AppScreen.CALL) {
                _lastJaaniSpokenText.value = reply
                if (_settings.value.ttsVoiceEnabled) {
                    voiceEngine.speak(reply)
                }
            }
        }
    }

    fun startListeningForCall() {
        voiceEngine.startListening(
            onResult = { spokenText ->
                _lastUserSpokenText.value = spokenText
                sendMessage(spokenText)
            },
            onError = { _ ->
                // Handled gracefully in UI state
            }
        )
    }

    fun stopListeningForCall() {
        voiceEngine.stopListening()
    }

    fun speakMessage(text: String) {
        voiceEngine.speak(text)
    }

    fun updateSettings(newSettings: UserSettings) {
        _settings.value = newSettings
        prefsManager.saveSettings(newSettings)
        voiceEngine.applySettings(newSettings)
    }

    fun testVoice(sampleText: String, pitch: Float, speed: Float) {
        voiceEngine.applySettings(_settings.value.copy(speechPitch = pitch, speechSpeed = speed))
        voiceEngine.speak(sampleText)
    }

    fun testNotification() {
        NotificationHelper.triggerRandomCheckIn(getApplication(), _settings.value.jaaniName)
    }

    fun clearChat() {
        viewModelScope.launch {
            chatDao.clearAll()
            initializeWelcomeMessageIfNeeded()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.release()
    }
}
