package com.example.data.models

import java.util.UUID

enum class ScreenState {
    AUTH,
    WELCOME,
    PREDICTOR
}

enum class GameMode(val displayName: String, val typeId: Int, val shortTitle: String) {
    WINGO("WINGO 1-MINUTE (TYPE 1)", 1, "55FIVE WINGO SIGNAL LOCK"),
    K3("K3 LOTRE 1-MINUTE (TYPE 9)", 9, "55FIVE K3 LOTRE SIGNAL LOCK"),
    FIVE_D("5D LOTRE 1-MINUTE (TYPE 5)", 5, "55FIVE 5D LOTRE SIGNAL LOCK"),
    TRX("TRX WINGO 1-MINUTE (TYPE 13)", 13, "55FIVE TRX WINGO SIGNAL LOCK")
}

data class UserAccount(
    val username: String,
    val passwordHash: String,
    val registeredAt: String
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val user: String,
    val text: String,
    val isSystem: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class PredictionResult(
    val issue: String,
    val digit: Int,
    val size: String,
    val color: String,
    val selectType: Int,
    val confidence: Int,
    val pattern: String,
    val drift: String
)

data class SeedCrackData(
    val seedHex: String,
    val matchRate: Double,
    val targetSig: String
)
