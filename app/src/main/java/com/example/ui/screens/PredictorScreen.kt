package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyOff
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.GameMode
import com.example.ui.CyberUiState
import com.example.ui.components.RadarSweepCanvas
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberDim
import com.example.ui.theme.CyberPanel
import com.example.ui.theme.CyberText
import com.example.ui.theme.CyberWhite
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonYellow

@Composable
fun PredictorScreen(
    state: CyberUiState,
    onLogout: () -> Unit,
    onToggleConfig: () -> Unit,
    onSaveToken: (String) -> Unit,
    onSetBypass: (Boolean) -> Unit,
    onSwitchGame: (GameMode) -> Unit,
    onRunSeedReverse: () -> Unit,
    onToggleChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var tempTokenInput by remember(state.manualToken) { mutableStateOf(state.manualToken) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. TOP HEADER PREDICTOR HUD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberPanel.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .border(1.dp, NeonCyan, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "CPU Icon",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "CYBER-X VIP 5.5",
                                color = CyberWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "USER: ${(state.currentUser ?: "GUEST").uppercase()}",
                                color = NeonGreen,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onToggleConfig,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonYellow.copy(alpha = 0.15f),
                                contentColor = NeonYellow
                            ),
                            border = BorderStroke(1.dp, NeonYellow.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            modifier = Modifier.testTag("btn_toggle_config")
                        ) {
                            Text(
                                text = "🔑 TOKEN MANUAL",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onLogout,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPink.copy(alpha = 0.15f),
                                contentColor = NeonPink
                            ),
                            border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            modifier = Modifier.testTag("btn_logout")
                        ) {
                            Text(
                                text = "LOGOUT",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. MANUAL TOKEN & API CONFIGURATION PANEL (Collapsible)
            AnimatedVisibility(
                visible = state.isConfigPanelOpen,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, NeonYellow), RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberBlack.copy(alpha = 0.98f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = "Key",
                                    tint = NeonYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "CONFIG MANUAL TOKEN & DIRECT API",
                                    color = NeonYellow,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(
                                onClick = onToggleConfig,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = CyberDim
                                )
                            }
                        }

                        Text(
                            text = "BEARER TOKEN (Paste JWT Token disini jika Auto Login Error):",
                            color = CyberDim,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        OutlinedTextField(
                            value = tempTokenInput,
                            onValueChange = { tempTokenInput = it },
                            placeholder = { Text("Paste Bearer JWT Token...", color = CyberDim, fontSize = 10.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .testTag("manual_token_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonYellow,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = NeonGreen,
                                unfocusedTextColor = NeonGreen,
                                focusedContainerColor = CyberDark,
                                unfocusedContainerColor = CyberDark
                            ),
                            shape = RoundedCornerShape(6.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onSaveToken(tempTokenInput) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_save_token"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonYellow.copy(alpha = 0.2f),
                                    contentColor = NeonYellow
                                ),
                                border = BorderStroke(1.dp, NeonYellow),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "SIMPAN TOKEN",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { onSetBypass(!state.isBypassMode) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_toggle_bypass"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.isBypassMode) NeonGreen.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.2f),
                                    contentColor = if (state.isBypassMode) NeonGreen else NeonCyan
                                ),
                                border = BorderStroke(1.dp, if (state.isBypassMode) NeonGreen else NeonCyan),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (state.isBypassMode) "MODE LOKAL (AKTIF)" else "MODE LOKAL (BYPASS)",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 3. SISTEM 1: SEED DISCOVERY PANEL (SIGNATURE BREAK ENGINE)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, NeonPink), RoundedCornerShape(10.dp))
                    .shadow(elevation = 8.dp, spotColor = NeonPink),
                colors = CardDefaults.cardColors(containerColor = CyberDark.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "Key",
                                tint = NeonPink,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "SISTEM 1: SEED RECONSTRUCTION ENGINE",
                                color = NeonPink,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(NeonPink.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .border(1.dp, NeonPink, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE CRACKING",
                                color = NeonPink,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberBlack)
                                .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "RECONSTRUCTED SEED:",
                                    color = CyberDim,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = state.seedData.seedHex,
                                    color = NeonGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberBlack)
                                .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "SIGNATURE MATCH:",
                                    color = CyberDim,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "${state.seedData.matchRate}% MATCH",
                                    color = NeonYellow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TARGET SIGNATURE MD5: 5232F775...",
                            color = CyberDim,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Button(
                            onClick = onRunSeedReverse,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPink.copy(alpha = 0.2f),
                                contentColor = NeonPink
                            ),
                            border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 8.dp,
                                vertical = 2.dp
                            ),
                            modifier = Modifier.testTag("btn_force_seed_reverse")
                        ) {
                            Text(
                                text = "⚡ RUN SEED REVERSE",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 4. GAME ENGINE SELECTOR
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberDark.copy(alpha = 0.9f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "SELECT GAME ENGINE:",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GameMode.entries.forEach { mode ->
                            val isSelected = state.activeGame == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else CyberPanel)
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonCyan else CyberBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { onSwitchGame(mode) }
                                    .padding(vertical = 8.dp, horizontal = 2.dp)
                                    .testTag("select_game_${mode.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode.name,
                                    color = if (isSelected) NeonCyan else CyberDim,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // 5. PREDICTION LOCK BOX WITH SISTEM 2 CONFIRMATION
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, NeonCyan), RoundedCornerShape(12.dp))
                    .shadow(elevation = 12.dp, spotColor = NeonCyan),
                colors = CardDefaults.cardColors(containerColor = CyberDark.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Issue Lock
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.activeGame.shortTitle,
                            color = CyberWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "ISSUE: ${state.currentPrediction?.issue ?: "SCANNING..."}",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Radar & Target Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        RadarSweepCanvas(
                            modifier = Modifier
                                .size(170.dp)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Sistem 2 badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .background(NeonGreen.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                    .border(1.dp, NeonGreen, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Verified",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "SISTEM 2: PREDICTION VERIFIED & LOCKED",
                                    color = NeonGreen,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "TARGET RESULT PREDICTION",
                                color = CyberDim,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            // Big Target Digit
                            Text(
                                text = state.currentPrediction?.digit?.toString() ?: "?",
                                color = NeonGreen,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )

                            // Target Class Badge
                            Box(
                                modifier = Modifier
                                    .background(NeonCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = state.currentPrediction?.let { "${it.size} (SINGLE ${it.digit})" } ?: "ANALYZING...",
                                    color = NeonCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pattern Analysis Details
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberBlack)
                            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "CONFIDENCE ACCURACY",
                                    color = CyberDim,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "${state.currentPrediction?.confidence ?: 0}%",
                                    color = NeonYellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            LinearProgressIndicator(
                                progress = { (state.currentPrediction?.confidence ?: 0) / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = NeonGreen,
                                trackColor = CyberPanel
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "POLA: ${state.currentPrediction?.pattern ?: "SCANNING..."}",
                                        color = CyberWhite,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "WARNA: ${state.currentPrediction?.color ?: "HIJAU 🟢"}",
                                        color = NeonCyan,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "TARGET: ${state.currentPrediction?.size ?: "BESAR"} (${state.currentPrediction?.digit ?: 7})",
                                        color = NeonGreen,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "CONFIRM DRIFT: ${state.currentPrediction?.drift ?: "0.02% (OK)"}",
                                        color = NeonGreen,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. LOG 1: OPERATIONAL LOG TERMINAL
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberBlack),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = "Terminal",
                                tint = NeonCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "LOG 1: OPERATIONAL TERMINAL",
                                color = NeonCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = if (state.isAnalyzing) "ANALYZING" else "IDLE",
                            color = if (state.isAnalyzing) NeonYellow else NeonGreen,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val termListState = rememberLazyListState()
                    LaunchedEffect(state.terminalLogs.size) {
                        if (state.terminalLogs.isNotEmpty()) {
                            termListState.animateScrollToItem(state.terminalLogs.size - 1)
                        }
                    }

                    LazyColumn(
                        state = termListState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(state.terminalLogs) { log ->
                            Text(
                                text = log,
                                color = if (log.contains("[SYS]") || log.contains("[NEW_ISSUE]")) NeonGreen else CyberDim,
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }

            // 7. LOG 2: PATTERN DETECTION & MOMENT SIGNAL RADAR
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .border(BorderStroke(1.dp, NeonYellow), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberBlack),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = "Radar",
                                tint = NeonYellow,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "LOG 2: PATTERN RADAR & SIGNALS",
                                color = NeonYellow,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "15 PERIODE DIPINDAI",
                            color = NeonGreen,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val radarListState = rememberLazyListState()
                    LaunchedEffect(state.radarSignals.size) {
                        if (state.radarSignals.isNotEmpty()) {
                            radarListState.animateScrollToItem(state.radarSignals.size - 1)
                        }
                    }

                    LazyColumn(
                        state = radarListState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        items(state.radarSignals) { signal ->
                            Text(
                                text = signal,
                                color = when {
                                    signal.contains("🔥") || signal.contains("⚡") -> NeonGreen
                                    signal.contains("🔀") -> NeonCyan
                                    else -> NeonYellow
                                },
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }

        // FLOATING LIVE CHAT BUTTON
        FloatingActionButton(
            onClick = onToggleChat,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("btn_toggle_chat"),
            containerColor = NeonCyan,
            contentColor = CyberBlack,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Message,
                contentDescription = "Chat",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
