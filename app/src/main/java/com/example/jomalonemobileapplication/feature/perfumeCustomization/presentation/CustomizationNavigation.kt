package com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class CustomizationScreen {
    QUESTIONS, NAME_INPUT, SUMMARY, THANK_YOU
}

// ONLY keep this data class:
data class CustomizationNavigationState(
    val currentScreen: CustomizationScreen = CustomizationScreen.QUESTIONS,
    val currentQuestionIndex: Int = 0
)
