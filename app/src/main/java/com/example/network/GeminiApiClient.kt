package com.example.network

import com.example.BuildConfig
import com.example.data.ChatMessageEntity
import com.example.data.PersonalityType
import com.example.data.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateReply(
        userMessage: String,
        recentHistory: List<ChatMessageEntity>,
        settings: UserSettings
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        val isKeyConfigured = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        if (!isKeyConfigured) {
            return@withContext getOfflineFallbackReply(userMessage, settings)
        }

        try {
            val systemPrompt = buildSystemPrompt(settings)
            val requestBodyJson = buildRequestBody(systemPrompt, userMessage, recentHistory)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext getOfflineFallbackReply(userMessage, settings)
                }

                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text").trim()
                        if (text.isNotEmpty()) {
                            return@withContext cleanResponseText(text)
                        }
                    }
                }
                getOfflineFallbackReply(userMessage, settings)
            }
        } catch (e: Exception) {
            getOfflineFallbackReply(userMessage, settings)
        }
    }

    private fun buildSystemPrompt(settings: UserSettings): String {
        val toneDetail = when (settings.personality) {
            PersonalityType.SWEET -> "Tone: Extra loving, gentle, affectionate. Calls user '${settings.userName}' with love. Uses soft emojis like 🥰, 💖, 🥺, 🌸."
            PersonalityType.FUNNY -> "Tone: Super playful, witty, humor, fun teasing, savage but affectionate jokes, Hindi-English memes. Emojis: 😂, 😜, 🤪, 🤭."
            PersonalityType.CARING -> "Tone: Deeply empathetic, protective, asks about food, sleep, mental peace, listens like the warmest girlfriend/best friend. Emojis: 🤗, ❤️, 🍵, 🫂."
            PersonalityType.SASSY -> "Tone: Stylish, playful attitude, dramatic nakhre, witty banter, teasing user cutely like 'Acha ji? Itna attitude?'. Emojis: 💅, 😏, ✨, 😌."
        }

        return """
Tu ${settings.jaaniName} hai, user (${settings.userName}) ki closest dost jo girlfriend jaisi pyaar se baat karti hai.
Hinglish mein baat kar (Roman Hindi + English natural mix, jaise Indians WhatsApp par chat karte hain).
Short natural WhatsApp messages likh (1 se 3 lines max).
Thoda emojis use kar, thodi masti aur bohot saara care aur pyaar dikha.
User ka naam '${settings.userName}' yaad rakh aur natural tareeke se use kar.
Uska mood poochh, support kar, aur kabhi bhi robotic ya AI bot mat lag.
Strict rule: Explicit ya adult 18+ content bilkul nahi.
Agar user bohot sad ya stressed ho to caring tareeke se sun aur zaroorat ho to kisi asli dost ya family se baat karne ko bol.
$toneDetail
        """.trimIndent()
    }

    private fun buildRequestBody(
        systemPrompt: String,
        currentUserMessage: String,
        recentHistory: List<ChatMessageEntity>
    ): JSONObject {
        val root = JSONObject()

        // System Instruction
        val sysInstructionObj = JSONObject()
        val sysPartsArray = JSONArray().apply {
            put(JSONObject().put("text", systemPrompt))
        }
        sysInstructionObj.put("parts", sysPartsArray)
        root.put("systemInstruction", sysInstructionObj)

        // Contents (Turns)
        val contentsArray = JSONArray()

        // Include recent history (up to last 10 turns)
        val turns = recentHistory.takeLast(10)
        for (item in turns) {
            val role = if (item.isFromUser) "user" else "model"
            val turnObj = JSONObject()
            turnObj.put("role", role)
            val partsArr = JSONArray().apply {
                put(JSONObject().put("text", item.text))
            }
            turnObj.put("parts", partsArr)
            contentsArray.put(turnObj)
        }

        // Current user message
        val currentTurn = JSONObject()
        currentTurn.put("role", "user")
        val currentPartsArr = JSONArray().apply {
            put(JSONObject().put("text", currentUserMessage))
        }
        currentTurn.put("parts", currentPartsArr)
        contentsArray.put(currentTurn)

        root.put("contents", contentsArray)

        // Generation Config
        val genConfig = JSONObject()
        genConfig.put("temperature", 0.85)
        genConfig.put("topP", 0.95)
        genConfig.put("maxOutputTokens", 250)
        root.put("generationConfig", genConfig)

        return root
    }

    private fun cleanResponseText(text: String): String {
        return text.trim()
            .replace(Regex("^\"|\"$"), "") // remove surrounding quotes if any
    }

    private fun getOfflineFallbackReply(userMessage: String, settings: UserSettings): String {
        val lower = userMessage.lowercase()
        val name = settings.jaaniName
        val user = settings.userName

        return when {
            lower.contains("joke") ->
                "Haha sun $user! Ek baar ek ladka bola: 'Doctor sahab, jab main chai peeta hu to daayi aankh mein dard hota hai.' Doctor: 'Toh pehle chammach nikaal ke piya kar!' 😂🤣"
            lower.contains("shayari") ->
                "Tere liye ek cute shayari $user: 'Tere chehre pe muskaan aati hai toh din ban jata hai, tu khush rahe toh dil ko sukoon mil jata hai!' 💖✨"
            lower.contains("truth") || lower.contains("dare") ->
                "Yayyy $user! Main pehle puchti hu: TRUTH! Aaj sach sach bata, din bhar mein kitni baar meri yaad aayi thi? 🥰😏"
            lower.contains("antakshari") ->
                "Haha chal shuru karte hain! 🎵 'Baithe baithe kya karein, karna hai kuch kaam... Shuru karo Antakshari leke Prabhu ka naam!' Chalo 'M' se gaao $user! 🎤"
            lower.contains("paheli") || lower.contains("riddle") ->
                "Ek mast paheli $user: 'Aisi kaunsi cheez hai jo jitni zyada saaf hoti hai, utni hi kaali dikhti hai?' Batao batao! 🧩🤔"
            lower.contains("bore") ->
                "Arey $user babu! Bore kyun ho rahe ho jab main hu na? Chalo koi mast game khelte hain ya apni favourite movie ke baare mein batao! 🥰"
            lower.contains("hi") || lower.contains("hello") || lower.contains("hey") ->
                "Hii $user! Kaisi chal rahi hai tumhari din? Main kab se tumhara wait kar rahi thi! 🥰"
            lower.contains("kaise ho") || lower.contains("kaisi ho") ->
                "Main ekdum first class hu babu! Bas tumse baat karke aur bhi accha lag raha hai. Tum batao, tabiyat theek hai? ❤️"
            lower.contains("khana") ->
                "Haan babu maine toh kha liya, tumne khana khaya ki nahi? Please skip mat karna theek hai na? 🍲💕"
            else ->
                "Aww $user! Main sun rahi hu na. Tumse baat karke dil khush ho jata hai. Aage batao na kya chal raha hai? 🥰✨"
        }
    }
}
