package com.example.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class SpeechInputState {
    IDLE,
    LISTENING,
    PROCESSING,
    ERROR
}

class VoiceEngine(private val context: Context) {
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private var speechRecognizer: SpeechRecognizer? = null

    private val _inputState = MutableStateFlow(SpeechInputState.IDLE)
    val inputState: StateFlow<SpeechInputState> = _inputState.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private var currentSettings: UserSettings = UserSettings()

    init {
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
                applyTtsLocale()
                applySettings(currentSettings)
                setupTtsListener()
            }
        }
    }

    private fun applyTtsLocale() {
        val ttsInstance = tts ?: return
        val hiLocale = Locale.forLanguageTag("hi-IN")
        val result = ttsInstance.isLanguageAvailable(hiLocale)
        if (result >= TextToSpeech.LANG_AVAILABLE) {
            ttsInstance.language = hiLocale
        } else {
            ttsInstance.language = Locale.ENGLISH
        }
    }

    private fun setupTtsListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }
        })
    }

    fun applySettings(settings: UserSettings) {
        currentSettings = settings
        if (isTtsReady) {
            tts?.setPitch(settings.speechPitch)
            tts?.setSpeechRate(settings.speechSpeed)
        }
    }

    fun speak(text: String) {
        if (!isTtsReady || text.isBlank()) return
        stopSpeaking()
        val cleanText = text.replace(Regex("[\\p{So}\\p{Cn}]"), "") // strip pure emoji symbols for cleaner TTS
        val utteranceId = "jaani_msg_${System.currentTimeMillis()}"
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition not available on device")
            _inputState.value = SpeechInputState.ERROR
            return
        }

        stopSpeaking()
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _inputState.value = SpeechInputState.LISTENING
                }

                override fun onBeginningOfSpeech() {
                    _inputState.value = SpeechInputState.LISTENING
                }

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    _inputState.value = SpeechInputState.PROCESSING
                }

                override fun onError(error: Int) {
                    _inputState.value = SpeechInputState.IDLE
                    onError("Voice input error: $error")
                }

                override fun onResults(results: Bundle?) {
                    _inputState.value = SpeechInputState.IDLE
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val spokenText = matches?.firstOrNull()?.trim().orEmpty()
                    if (spokenText.isNotEmpty()) {
                        onResult(spokenText)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Jaani se baat karo...")
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _inputState.value = SpeechInputState.IDLE
            onError(e.message ?: "Failed to start speech recognizer")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        _inputState.value = SpeechInputState.IDLE
    }

    fun release() {
        stopSpeaking()
        tts?.shutdown()
        tts = null
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
