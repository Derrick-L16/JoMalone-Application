package com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation

enum class CustomizationScreen {
    QUESTIONS, NAME_INPUT, SUMMARY, THANK_YOU
}

data class CustomizationNavigationState(
    val currentScreen: CustomizationScreen = CustomizationScreen.QUESTIONS,
    val currentQuestionIndex: Int = 0
)
