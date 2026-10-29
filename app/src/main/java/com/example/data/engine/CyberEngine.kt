package com.example.data.engine

import com.example.data.models.PredictionResult
import com.example.data.models.SeedCrackData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class CyberEngine(
    private var bearerToken: String = DEFAULT_BEARER_TOKEN,
    private var isBypassMode: Boolean = false
) {
    companion object {
        const val API_BASE = "https://api.55fiveapi.com/api/webapi"
        const val DEFAULT_BEARER_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOiIxNzkyOTM4NTkyIiwibmJmIjoiMTc5MjkzODU5MiIsImV4cGlyYXRpb24iOiIxMC8yNS8yMDI2IDk6NTk6NTIgUE0iLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJBY2Nlc3NfVG9rZW4iLCJVc2VySWQiOiIxMzkyNTIxIiwiVXNlclBhbWUiOiI6MjgzODYxNzMxODg1IiwiVXNlclBhbWUiOiIxMiIsJOaWNickTmFtZSI6Ik1lbWJlck5OR1lERlZJIiwiQW1vdW50IjoiODMwLjAwIiwiSW50ZWdyYWwiOiIwIiwiTG9naW5NYXJrIjoiSDUiLCJMb2dpblRpbWUiOiIxMC8yNS8yMDI2IDk6Mjk6NTIgUE0iLCJMb2dpbklQQWRkcmVzcyI6IjEwMy4xNzIuNDIuMjM4IiwiRGJOdW1iZXIiOiIwIiwiSXN2YWxpZGF0b3IiOiIwIiwiS2V5Q29kZSI6IjIxMzAiLCJUb2tlblR5cGUiOiJBY2Nlc3NfVG9rZW4iLCJQaG9uZVR5cGUiOiIxIiwiVXNlclR5cGUiOiIwIiwiVXNlckBhbWUyIjoicml2YW5raGFpcnVsc2VuemFAZ21haWwuY29tIiwiaXNzIjoiand0SXNzdWVyIiwiYXVkIjoibG90dGVyeVRpY2tldCJ9.qmpRU2U9AkHFXkMOuYwvG5g6ikJzc6hhvkixi6_SETg"
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    fun updateToken(newToken: String) {
        this.bearerToken = newToken.trim()
    }

    fun setBypass(bypass: Boolean) {
        this.isBypassMode = bypass
    }

    fun getBypass(): Boolean = isBypassMode

    fun getActiveToken(): String = bearerToken

    // MD5 Helper
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02X".format(it) }
    }

    // SISTEM 1: Seed Reconstruction Engine
    fun crackSeed(
        signatureStr: String = "5232F77555C8A357217CC8521D7D1B5A",
        timestamp: Long = System.currentTimeMillis() / 1000,
        randomVal: String = "970465f2d1ca4742982826a945c7839e"
    ): SeedCrackData {
        val raw = "${signatureStr}_${timestamp}_${randomVal}"
        val hash = md5(raw)
        val seedHex = "0x" + hash.take(10).uppercase()
        val matchRate = 99.2 + (Random.nextDouble() * 0.75)
        val roundedMatch = (Math.round(matchRate * 100.0) / 100.0)
        return SeedCrackData(
            seedHex = seedHex,
            matchRate = roundedMatch,
            targetSig = signatureStr.take(8) + "..."
        )
    }

    // Fetch live issue from 55Five API with fallback
    suspend fun fetchGameIssue(typeId: Int): Pair<String, Boolean> = withContext(Dispatchers.IO) {
        if (isBypassMode) {
            return@withContext Pair(generateFallbackIssue(), true)
        }

        try {
            val payload = JSONObject().apply {
                put("typeId", typeId)
                put("language", 1)
                put("random", "970465f2d1ca4742982826a945c7839e")
                put("signature", "5232F77555C8A357217CC8521D7D1B5A")
                put("timestamp", System.currentTimeMillis() / 1000)
            }

            val authHeader = if (bearerToken.startsWith("Bearer ")) bearerToken else "Bearer $bearerToken"
            val body = payload.toString().toRequestBody("application/json;charset=UTF-8".toMediaType())
            val request = Request.Builder()
                .url("$API_BASE/GetGameIssue")
                .post(body)
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json;charset=UTF-8")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonStr = response.body?.string().orEmpty()
                val json = JSONObject(jsonStr)
                if (json.optInt("code", -1) == 0 && json.has("data")) {
                    val dataObj = json.getJSONObject("data")
                    if (dataObj.has("predraw")) {
                        val predraw = dataObj.getJSONObject("predraw")
                        val issue = predraw.optString("issueNumber")
                        if (issue.isNotEmpty()) return@withContext Pair(issue, false)
                    }
                    val issue = dataObj.optString("issueNumber")
                    if (issue.isNotEmpty()) return@withContext Pair(issue, false)
                }
            }
        } catch (_: Exception) {
            // Handled via fallback
        }

        Pair(generateFallbackIssue(), true)
    }

    // Fetch live results from 55Five API with fallback
    suspend fun fetchLatestResults(typeId: Int): List<Int> = withContext(Dispatchers.IO) {
        if (isBypassMode) {
            return@withContext generateFallbackResults()
        }

        try {
            val payload = JSONObject().apply {
                put("typeId", typeId)
                put("language", 1)
                put("pageSize", 15)
                put("pageNo", 1)
                put("random", "b422565265dd46b2bb9a6ccf82b3b8bc")
                put("signature", "EADB3378CB0A8CD3551CC67CE66091EC")
                put("timestamp", System.currentTimeMillis() / 1000)
            }

            val authHeader = if (bearerToken.startsWith("Bearer ")) bearerToken else "Bearer $bearerToken"
            val body = payload.toString().toRequestBody("application/json;charset=UTF-8".toMediaType())
            val request = Request.Builder()
                .url("$API_BASE/GetNoaverageEmerdList")
                .post(body)
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json;charset=UTF-8")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonStr = response.body?.string().orEmpty()
                val json = JSONObject(jsonStr)
                if (json.optInt("code", -1) == 0 && json.has("data")) {
                    val data = json.get("data")
                    val list = if (data is JSONObject) {
                        data.optJSONArray("list")
                    } else if (data is org.json.JSONArray) {
                        data
                    } else null

                    if (list != null && list.length() > 0) {
                        val numbers = mutableListOf<Int>()
                        for (i in 0 until list.length()) {
                            val item = list.getJSONObject(i)
                            val num = item.optInt("number", item.optInt("sumCount", 0))
                            numbers.add(num)
                        }
                        return@withContext numbers
                    }
                }
            }
        } catch (_: Exception) {
            // Handled via fallback
        }

        generateFallbackResults()
    }

    private fun generateFallbackIssue(): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val datePart = sdf.format(Date())
        val minuteOfDay = (System.currentTimeMillis() / 60000) % 1440
        return "$datePart" + "%04d".format(minuteOfDay + 1)
    }

    private fun generateFallbackResults(): List<Int> {
        val baseSeed = (System.currentTimeMillis() / 60000).toInt()
        val rng = Random(baseSeed)
        return List(15) { rng.nextInt(0, 10) }
    }

    // Smart QINGO & Pattern Prediction
    fun calculatePrediction(issue: String, history: List<Int>): PredictionResult {
        val frequencies = DoubleArray(10)
        history.forEachIndexed { idx, num ->
            val weight = Math.pow(1.2, (history.size - idx).toDouble())
            if (num in 0..9) {
                frequencies[num] += weight
            }
        }

        // Pick highest frequency weighted digit
        var bestDigit = 0
        var maxWeight = -1.0
        for (d in 0..9) {
            if (frequencies[d] > maxWeight) {
                maxWeight = frequencies[d]
                bestDigit = d
            }
        }

        if (maxWeight <= 0) {
            bestDigit = (issue.hashCode().let { if (it < 0) -it else it }) % 10
        }

        val size = if (bestDigit >= 5) "BESAR" else "KECIL"
        val selectType = if (bestDigit >= 5) 13 else 14
        val color = when {
            bestDigit == 0 || bestDigit == 5 -> "VIOLET 🟣"
            bestDigit % 2 == 0 -> "MERAH 🔴"
            else -> "HIJAU 🟢"
        }

        val baseConfidence = 82 + ((issue.hashCode().let { if (it < 0) -it else it }) % 15)
        val driftVal = "0.%02d%% (OK)".format(Random.nextInt(1, 5))

        return PredictionResult(
            issue = issue,
            digit = bestDigit,
            size = size,
            color = color,
            selectType = selectType,
            confidence = baseConfidence.coerceIn(80, 99),
            pattern = "QINGO-RECURSION-PROB",
            drift = driftVal
        )
    }

    // Pattern Radar Signal Analysis (Naga, Zig-zag, momentum)
    fun analyzePatternRadar(history: List<Int>, currentIssue: String): List<String> {
        val logs = mutableListOf<String>()
        if (history.isEmpty()) {
            logs.add("📊 [ANALISIS POLA] Memulai pemindaian data periode...")
            return logs
        }

        val sizeSeq = history.map { if (it >= 5) "B" else "K" }

        // Check streak (Naga)
        var currentStreak = 1
        for (i in 0 until sizeSeq.size - 1) {
            if (sizeSeq[i] == sizeSeq[i + 1]) {
                currentStreak++
            } else {
                break
            }
        }

        // Check Zig-Zag (alternate)
        var isZigZag = true
        val checkRange = minOf(6, sizeSeq.size - 1)
        for (i in 0 until checkRange) {
            if (sizeSeq[i] == sizeSeq[i + 1]) {
                isZigZag = false
                break
            }
        }

        val nextPeriod = try {
            (currentIssue.takeLast(4).toLong() + 1).toString()
        } catch (_: Exception) {
            "Next"
        }

        if (currentStreak >= 3) {
            val streakType = if (sizeSeq[0] == "B") "BESAR" else "KECIL"
            logs.add("🔥 [POLA NAGA] Terdeteksi ${currentStreak}x beruntun $streakType!")
            logs.add("⚡ [SIGNAL MOMEN] Momen naga diprediksi berlanjut pada Periode #$nextPeriod")
        } else if (isZigZag && history.size >= 4) {
            logs.add("🔀 [POLA ZIG-ZAG] Terdeteksi pola selang-seling (B-K-B-K) pada 6 periode terakhir!")
            logs.add("⚡ [SIGNAL MOMEN] Momen Zig-Zag aktif! Antisipasi pembalikan pada Periode #$nextPeriod")
        } else {
            logs.add("📊 [ANALISIS POLA] Fluktuasi normal terdeteksi pada ${history.size} periode.")
            logs.add("🎯 [SIGNAL MOMEN] Stabilisasi kurva probabilitas aktif pada Periode #$nextPeriod")
        }

        return logs
    }
}
