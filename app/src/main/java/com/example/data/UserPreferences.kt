package com.example.data

import android.content.Context
import android.content.SharedPreferences

enum class PersonalityType(val displayName: String, val tagline: String, val emoji: String) {
    SWEET("Sweet", "Pyaari, gentle aur loving baatein", "🥰"),
    FUNNY("Funny", "Masti, meme reference aur jokes", "😂"),
    CARING("Caring", "Khayal rakhne wali, supportive aur protective", "🤗"),
    SASSY("Sassy", "Nakhre, stylish attitude aur witty teasing", "💅")
}

data class UserSettings(
    val jaaniName: String = "Jaani",
    val userName: String = "Babu",
    val personality: PersonalityType = PersonalityType.SWEET,
    val ttsVoiceEnabled: Boolean = true,
    val speechPitch: Float = 1.15f,
    val speechSpeed: Float = 1.05f,
    val checkInsEnabled: Boolean = true
)

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("jaani_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_JAANI_NAME = "key_jaani_name"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_PERSONALITY = "key_personality"
        private const val KEY_TTS_ENABLED = "key_tts_enabled"
        private const val KEY_SPEECH_PITCH = "key_speech_pitch"
        private const val KEY_SPEECH_SPEED = "key_speech_speed"
        private const val KEY_CHECKINS_ENABLED = "key_checkins_enabled"
    }

    fun getSettings(): UserSettings {
        val personalityName = prefs.getString(KEY_PERSONALITY, PersonalityType.SWEET.name) ?: PersonalityType.SWEET.name
        val personality = try {
            PersonalityType.valueOf(personalityName)
        } catch (e: Exception) {
            PersonalityType.SWEET
        }
        return UserSettings(
            jaaniName = prefs.getString(KEY_JAANI_NAME, "Jaani") ?: "Jaani",
            userName = prefs.getString(KEY_USER_NAME, "Babu") ?: "Babu",
            personality = personality,
            ttsVoiceEnabled = prefs.getBoolean(KEY_TTS_ENABLED, true),
            speechPitch = prefs.getFloat(KEY_SPEECH_PITCH, 1.15f),
            speechSpeed = prefs.getFloat(KEY_SPEECH_SPEED, 1.05f),
            checkInsEnabled = prefs.getBoolean(KEY_CHECKINS_ENABLED, true)
        )
    }

    fun saveSettings(settings: UserSettings) {
        prefs.edit()
            .putString(KEY_JAANI_NAME, settings.jaaniName.trim().ifEmpty { "Jaani" })
            .putString(KEY_USER_NAME, settings.userName.trim().ifEmpty { "Babu" })
            .putString(KEY_PERSONALITY, settings.personality.name)
            .putBoolean(KEY_TTS_ENABLED, settings.ttsVoiceEnabled)
            .putFloat(KEY_SPEECH_PITCH, settings.speechPitch)
            .putFloat(KEY_SPEECH_SPEED, settings.speechSpeed)
            .putBoolean(KEY_CHECKINS_ENABLED, settings.checkInsEnabled)
            .apply()
    }
}
