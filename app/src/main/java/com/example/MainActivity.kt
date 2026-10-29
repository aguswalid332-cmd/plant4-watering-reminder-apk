package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.models.ScreenState
import com.example.ui.CyberViewModel
import com.example.ui.components.ChatBottomSheet
import com.example.ui.components.MatrixBackground
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.PredictorScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: CyberViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                // Back navigation handling
                BackHandler(enabled = uiState.screen != ScreenState.AUTH) {
                    when (uiState.screen) {
                        ScreenState.PREDICTOR -> viewModel.logout()
                        ScreenState.WELCOME -> viewModel.logout()
                        ScreenState.AUTH -> { /* Exit */ }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = CyberBlack,
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Animated cyberpunk digital rain matrix background
                        MatrixBackground()

                        // Screen navigation with vortex-like scaling transition
                        AnimatedContent(
                            targetState = uiState.screen,
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.85f, animationSpec = tween(500)))
                                    .togetherWith(fadeOut(animationSpec = tween(400)) + scaleOut(targetScale = 1.15f, animationSpec = tween(400)))
                            },
                            label = "screen_transition"
                        ) { screen ->
                            when (screen) {
                                ScreenState.AUTH -> {
                                    AuthScreen(
                                        errorMessage = uiState.authError,
                                        successMessage = uiState.authSuccess,
                                        onRegister = { user, pass, confirm ->
                                            viewModel.register(user, pass, confirm)
                                        },
                                        onLogin = { user, pass ->
                                            viewModel.login(user, pass)
                                        }
                                    )
                                }
                                ScreenState.WELCOME -> {
                                    WelcomeScreen(
                                        username = uiState.currentUser,
                                        isNetworkOnline = uiState.isNetworkOnline,
                                        onEnterPredictor = {
                                            viewModel.activateWelcomeToPredictor()
                                        }
                                    )
                                }
                                ScreenState.PREDICTOR -> {
                                    PredictorScreen(
                                        state = uiState,
                                        onLogout = { viewModel.logout() },
                                        onToggleConfig = { viewModel.toggleConfigPanel() },
                                        onSaveToken = { token -> viewModel.saveManualToken(token) },
                                        onSetBypass = { bypass -> viewModel.setBypassMode(bypass) },
                                        onSwitchGame = { game -> viewModel.switchGame(game) },
                                        onRunSeedReverse = { viewModel.runForceSeedReverse() },
                                        onToggleChat = { viewModel.toggleChat() }
                                    )
                                }
                            }
                        }

                        // Live Chat Community Bottom Sheet
                        if (uiState.isChatOpen) {
                            ChatBottomSheet(
                                messages = uiState.chatMessages,
                                onSendMessage = { text -> viewModel.sendChatMessage(text) },
                                onDismiss = { viewModel.toggleChat(false) }
                            )
                        }
                    }
                }
            }
        }
    }
}
