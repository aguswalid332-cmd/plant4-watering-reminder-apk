package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.ChatMessage
import com.example.data.models.PredictionResult
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CyberRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("cyber_vip_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USERS = "cyber_users_json"
        private const val KEY_SESSION_USER = "cyber_session_user"
        private const val KEY_SESSION_TIME = "cyber_session_time"
        private const val KEY_MANUAL_TOKEN = "cyber_manual_token"
        private const val KEY_BYPASS_MODE = "cyber_bypass_mode"
        private const val KEY_CHAT_MESSAGES = "cyber_chat_json"
        private const val KEY_PRED_CACHE = "cyber_pred_cache_"
    }

    // Auth
    fun getUsers(): Map<String, String> {
        val jsonStr = prefs.getString(KEY_USERS, "{}") ?: "{}"
        val map = mutableMapOf<String, String>()
        try {
            val json = JSONObject(jsonStr)
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = json.getString(key)
            }
        } catch (_: Exception) {}
        return map
    }

    fun registerUser(username: String, password: String): Boolean {
        val users = getUsers().toMutableMap()
        if (users.containsKey(username)) {
            return false
        }
        users[username] = password
        val json = JSONObject(users as Map<*, *>)
        prefs.edit().putString(KEY_USERS, json.toString()).apply()
        return true
    }

    fun validateLogin(username: String, password: String): Boolean {
        val users = getUsers()
        return users[username] == password
    }

    fun saveSession(username: String) {
        prefs.edit()
            .putString(KEY_SESSION_USER, username)
            .putLong(KEY_SESSION_TIME, System.currentTimeMillis())
            .apply()
    }

    fun getActiveSessionUser(): String? {
        val user = prefs.getString(KEY_SESSION_USER, null) ?: return null
        val time = prefs.getLong(KEY_SESSION_TIME, 0L)
        val elapsedHours = (System.currentTimeMillis() - time) / (1000 * 60 * 60)
        return if (elapsedHours < 24) user else null
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_SESSION_USER)
            .remove(KEY_SESSION_TIME)
            .apply()
    }

    // Manual Token & Bypass
    fun getSavedToken(): String? {
        return prefs.getString(KEY_MANUAL_TOKEN, null)
    }

    fun saveManualToken(token: String) {
        prefs.edit().putString(KEY_MANUAL_TOKEN, token).apply()
    }

    fun isBypassMode(): Boolean {
        return prefs.getBoolean(KEY_BYPASS_MODE, false)
    }

    fun setBypassMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BYPASS_MODE, enabled).apply()
    }

    // Prediction per-issue cache
    fun getCachedPrediction(gameId: String, issue: String): PredictionResult? {
        val key = "$KEY_PRED_CACHE${gameId}_$issue"
        val jsonStr = prefs.getString(key, null) ?: return null
        return try {
            val json = JSONObject(jsonStr)
            PredictionResult(
                issue = json.getString("issue"),
                digit = json.getInt("digit"),
                size = json.getString("size"),
                color = json.getString("color"),
                selectType = json.getInt("selectType"),
                confidence = json.getInt("confidence"),
                pattern = json.getString("pattern"),
                drift = json.getString("drift")
            )
        } catch (_: Exception) {
            null
        }
    }

    fun cachePrediction(gameId: String, result: PredictionResult) {
        val key = "$KEY_PRED_CACHE${gameId}_${result.issue}"
        val json = JSONObject().apply {
            put("issue", result.issue)
            put("digit", result.digit)
            put("size", result.size)
            put("color", result.color)
            put("selectType", result.selectType)
            put("confidence", result.confidence)
            put("pattern", result.pattern)
            put("drift", result.drift)
        }
        prefs.edit().putString(key, json.toString()).apply()
    }

    // Live Chat Messages
    fun getChatMessages(): List<ChatMessage> {
        val jsonStr = prefs.getString(KEY_CHAT_MESSAGES, null)
        if (jsonStr.isNullOrEmpty()) {
            return listOf(
                ChatMessage(user = "AdminVIP", text = "Selamat datang di Cyber-X Predictor 5.5!"),
                ChatMessage(user = "MemberPro", text = "Sistem 1 & Sistem 2 aktif presisi!")
            )
        }
        return try {
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<ChatMessage>()
            for (i in 0 until arr.length()) {
                val item = arr.getJSONObject(i)
                list.add(
                    ChatMessage(
                        id = item.optString("id"),
                        user = item.getString("user"),
                        text = item.getString("text"),
                        isSystem = item.optBoolean("isSystem", false),
                        timestamp = item.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (_: Exception) {
            listOf(
                ChatMessage(user = "AdminVIP", text = "Selamat datang di Cyber-X Predictor 5.5!"),
                ChatMessage(user = "MemberPro", text = "Sistem 1 & Sistem 2 aktif presisi!")
            )
        }
    }

    fun addChatMessage(msg: ChatMessage) {
        val current = getChatMessages().toMutableList()
        current.add(msg)
        // Keep last 50
        val trimmed = if (current.size > 50) current.takeLast(50) else current
        val arr = JSONArray()
        for (m in trimmed) {
            val obj = JSONObject().apply {
                put("id", m.id)
                put("user", m.user)
                put("text", m.text)
                put("isSystem", m.isSystem)
                put("timestamp", m.timestamp)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_CHAT_MESSAGES, arr.toString()).apply()
    }

    fun formatIndonesianTimestamp(date: Date = Date()): String {
        val dayNames = arrayOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
        val cal = java.util.Calendar.getInstance()
        cal.time = date
        val dayName = dayNames[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1]
        val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sdfTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return "$dayName, ${sdfDate.format(date)} - ${sdfTime.format(date)} WIB"
    }
}
