package com.example.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CyberRepository
import com.example.data.engine.CyberEngine
import com.example.data.models.ChatMessage
import com.example.data.models.GameMode
import com.example.data.models.PredictionResult
import com.example.data.models.ScreenState
import com.example.data.models.SeedCrackData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CyberUiState(
    val screen: ScreenState = ScreenState.AUTH,
    val currentUser: String? = null,
    val isNetworkOnline: Boolean = true,
    val activeGame: GameMode = GameMode.WINGO,
    val manualToken: String = CyberEngine.DEFAULT_BEARER_TOKEN,
    val isBypassMode: Boolean = false,
    val isConfigPanelOpen: Boolean = false,
    val isChatOpen: Boolean = false,
    val authError: String? = null,
    val authSuccess: String? = null,
    val seedData: SeedCrackData = SeedCrackData("0x7F9A2B4C8E", 99.85, "5232F775..."),
    val currentPrediction: PredictionResult? = null,
    val isAnalyzing: Boolean = false,
    val radarSignals: List<String> = emptyList(),
    val terminalLogs: List<String> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList()
)

class CyberViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CyberRepository(application)
    private val engine = CyberEngine()

    private val _uiState = MutableStateFlow(CyberUiState())
    val uiState: StateFlow<CyberUiState> = _uiState.asStateFlow()

    private var engineJob: Job? = null
    private var lastAnalyzedIssue: String = ""

    init {
        // Load saved token and bypass preference
        val savedToken = repository.getSavedToken() ?: CyberEngine.DEFAULT_BEARER_TOKEN
        val bypass = repository.isBypassMode()
        engine.updateToken(savedToken)
        engine.setBypass(bypass)

        val activeUser = repository.getActiveSessionUser()
        val initialScreen = if (activeUser != null) ScreenState.WELCOME else ScreenState.AUTH

        _uiState.update {
            it.copy(
                screen = initialScreen,
                currentUser = activeUser,
                manualToken = savedToken,
                isBypassMode = bypass,
                chatMessages = repository.getChatMessages(),
                terminalLogs = listOf(
                    "[${getCurrentTimeStr()}] [SYS] VIP 5.5 Engine Connected.",
                    "[${getCurrentTimeStr()}] [AUTH] Security handshake validated."
                ),
                radarSignals = listOf(
                    "[${getCurrentTimeStr()}] [RADAR] Memulai pemindaian pola naga & zig-zag..."
                )
            )
        }

        checkNetworkStatus()
    }

    private fun getCurrentTimeStr(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    fun checkNetworkStatus() {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val isOnline = cm?.let {
            val net = it.activeNetwork
            val caps = it.getNetworkCapabilities(net)
            caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } ?: true
        _uiState.update { it.copy(isNetworkOnline = isOnline) }
    }

    fun register(username: String, password: String, confirm: String) {
        val trimmedUser = username.trim()
        val trimmedPass = password.trim()
        val trimmedConfirm = confirm.trim()

        if (trimmedUser.isEmpty() || trimmedPass.isEmpty()) {
            _uiState.update { it.copy(authError = "Username dan password tidak boleh kosong!", authSuccess = null) }
            return
        }

        if (trimmedPass != trimmedConfirm) {
            _uiState.update { it.copy(authError = "Konfirmasi password tidak cocok!", authSuccess = null) }
            return
        }

        val success = repository.registerUser(trimmedUser, trimmedPass)
        if (!success) {
            _uiState.update {
                it.copy(
                    authError = "⚠ Nama \"$trimmedUser\" sudah digunakan! Silakan gunakan username lain.",
                    authSuccess = null
                )
            }
            return
        }

        // Broadcast welcome message to chat
        val regTimestamp = repository.formatIndonesianTimestamp()
        val welcomeMsg = ChatMessage(
            user = "SYSTEM_BROADCAST",
            text = "🎉 Selamat datang $trimmedUser! [Terdaftar: $regTimestamp]",
            isSystem = true
        )
        repository.addChatMessage(welcomeMsg)

        _uiState.update {
            it.copy(
                authError = null,
                authSuccess = "Akun berhasil dibuat! Silakan Login.",
                chatMessages = repository.getChatMessages()
            )
        }
    }

    fun login(username: String, password: String) {
        val trimmedUser = username.trim()
        val trimmedPass = password.trim()

        if (trimmedUser.isEmpty() || trimmedPass.isEmpty()) {
            _uiState.update { it.copy(authError = "Username dan password harus diisi!", authSuccess = null) }
            return
        }

        if (!repository.validateLogin(trimmedUser, trimmedPass)) {
            _uiState.update { it.copy(authError = "Username atau Password salah!", authSuccess = null) }
            return
        }

        repository.saveSession(trimmedUser)
        _uiState.update {
            it.copy(
                currentUser = trimmedUser,
                screen = ScreenState.WELCOME,
                authError = null,
                authSuccess = null
            )
        }
    }

    fun logout() {
        repository.clearSession()
        stopEngineCycle()
        _uiState.update {
            it.copy(
                currentUser = null,
                screen = ScreenState.AUTH,
                authError = null,
                authSuccess = null
            )
        }
    }

    fun activateWelcomeToPredictor() {
        _uiState.update { it.copy(screen = ScreenState.PREDICTOR) }
        startEngineCycle()
    }

    fun switchGame(game: GameMode) {
        if (_uiState.value.activeGame == game) return
        _uiState.update { it.copy(activeGame = game) }
        logTerminal("[ENGINE] Active Game Engine Switched -> ${game.name}")
        lastAnalyzedIssue = ""
        triggerEngineStep()
    }

    fun runForceSeedReverse() {
        val cracked = engine.crackSeed()
        _uiState.update { it.copy(seedData = cracked) }
        logTerminal("[SEED] Force reverse-engineering engine executed! Seed: ${cracked.seedHex}")
    }

    fun toggleConfigPanel() {
        _uiState.update { it.copy(isConfigPanelOpen = !it.isConfigPanelOpen) }
    }

    fun saveManualToken(token: String) {
        val trimmed = token.trim()
        if (trimmed.isNotEmpty()) {
            engine.updateToken(trimmed)
            engine.setBypass(false)
            repository.saveManualToken(trimmed)
            repository.setBypassMode(false)
            _uiState.update {
                it.copy(
                    manualToken = trimmed,
                    isBypassMode = false,
                    isConfigPanelOpen = false
                )
            }
            logTerminal("[TOKEN] Manual Bearer Token Saved Successfully!")
            triggerEngineStep()
        }
    }

    fun setBypassMode(enabled: Boolean) {
        engine.setBypass(enabled)
        repository.setBypassMode(enabled)
        _uiState.update {
            it.copy(
                isBypassMode = enabled,
                isConfigPanelOpen = false
            )
        }
        val msg = if (enabled) "[BYPASS] Activated Local Prediction Engine Mode!" else "[BYPASS] Deactivated Local Mode"
        logTerminal(msg)
        triggerEngineStep()
    }

    fun toggleChat(open: Boolean? = null) {
        _uiState.update { it.copy(isChatOpen = open ?: !it.isChatOpen) }
    }

    fun sendChatMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val username = _uiState.value.currentUser ?: "GuestUser"
        val msg = ChatMessage(user = username, text = trimmed)
        repository.addChatMessage(msg)
        _uiState.update { it.copy(chatMessages = repository.getChatMessages()) }
    }

    private fun logTerminal(msg: String) {
        val time = getCurrentTimeStr()
        val entry = "[$time] $msg"
        _uiState.update {
            val logs = (it.terminalLogs + entry).takeLast(30)
            it.copy(terminalLogs = logs)
        }
    }

    private fun startEngineCycle() {
        stopEngineCycle()
        engineJob = viewModelScope.launch {
            while (isActive) {
                runEngineStep()
                delay(3000L)
            }
        }
    }

    private fun stopEngineCycle() {
        engineJob?.cancel()
        engineJob = null
    }

    private fun triggerEngineStep() {
        viewModelScope.launch {
            runEngineStep()
        }
    }

    private suspend fun runEngineStep() {
        val state = _uiState.value
        _uiState.update { it.copy(isAnalyzing = true) }

        val activeGame = state.activeGame
        val (issue, isFallback) = engine.fetchGameIssue(activeGame.typeId)
        val history = engine.fetchLatestResults(activeGame.typeId)

        // Seed discovery update
        val cracked = engine.crackSeed()

        // Check if prediction already locked in cache
        var pred = repository.getCachedPrediction(activeGame.name, issue)
        if (pred == null) {
            pred = engine.calculatePrediction(issue, history)
            repository.cachePrediction(activeGame.name, pred)
            logTerminal("[NEW_ISSUE] Periode $issue locked in storage!")
        }

        // Radar pattern detection
        val radarList = engine.analyzePatternRadar(history, issue)
        val time = getCurrentTimeStr()
        val formattedRadar = radarList.map { "[$time] $it" }

        _uiState.update { current ->
            val updatedLogs = if (issue != lastAnalyzedIssue && isFallback && !current.isBypassMode) {
                (current.terminalLogs + "[$time] [INFO] Standby sync live connection...").takeLast(30)
            } else {
                current.terminalLogs
            }

            current.copy(
                seedData = cracked,
                currentPrediction = pred,
                isAnalyzing = false,
                terminalLogs = updatedLogs,
                radarSignals = formattedRadar
            )
        }

        lastAnalyzedIssue = issue
    }

    override fun onCleared() {
        super.onCleared()
        stopEngineCycle()
    }
}
