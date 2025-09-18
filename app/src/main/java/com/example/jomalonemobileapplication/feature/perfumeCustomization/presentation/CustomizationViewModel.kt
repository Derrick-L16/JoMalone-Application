package com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.core.data.entity.CustomizationEntity
import com.example.jomalonemobileapplication.feature.perfumeCustomization.data.repository.CustomizationHistoryRepository
import com.example.jomalonemobileapplication.feature.perfumeCustomization.data.repository.CustomizationRepository
import com.example.jomalonemobileapplication.feature.perfumeCustomization.domain.model.CustomizationOption
import com.example.jomalonemobileapplication.feature.perfumeCustomization.domain.model.ScentLayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CustomizationState(
    val selectedOptions: Map<ScentLayer, CustomizationOption> = emptyMap(),
    val customPerfumeName: String = "",
    val isCustomizationComplete: Boolean = false
)


class CustomizationViewModel(
    private val customizationHistoryRepository: CustomizationHistoryRepository? = null,
    private val userId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomizationState())
    val uiState: StateFlow<CustomizationState> = _uiState.asStateFlow()

    private val _navigationState = MutableStateFlow(CustomizationNavigationState())
    val navigationState: StateFlow<CustomizationNavigationState> = _navigationState.asStateFlow()

    val questions = CustomizationRepository.getCustomizationQuestions()

    private val _showConfirmationDialog = MutableStateFlow(false)
    val showConfirmationDialog: StateFlow<Boolean> = _showConfirmationDialog.asStateFlow()

    private val _customizationHistory = MutableStateFlow<List<CustomizationEntity>>(emptyList())
    val customizationHistory: StateFlow<List<CustomizationEntity>> = _customizationHistory.asStateFlow()

    init {
        loadCustomizationHistory()
    }

    fun selectOption(layerType: ScentLayer, option: CustomizationOption){
        viewModelScope.launch {
            val currentState = _uiState.value
            val newSelections = currentState.selectedOptions + (layerType to option)

            _uiState.value = currentState.copy(selectedOptions = newSelections)

        }
    }

    fun getCurrentProgress(): Float {
        val totalSteps = questions.size + 2
        val currentNavState = _navigationState.value

        return when (currentNavState.currentScreen) {
            CustomizationScreen.QUESTIONS -> (currentNavState.currentQuestionIndex + 1).toFloat() / totalSteps
            CustomizationScreen.NAME_INPUT -> (questions.size + 1).toFloat() / totalSteps
            CustomizationScreen.SUMMARY -> (questions.size + 2).toFloat() / totalSteps
            CustomizationScreen.THANK_YOU -> 1.0f
        }
    }

    fun updatePerfumeName(name: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.value = currentState.copy(customPerfumeName = name)
        }
    }


    fun moveToPreviousQuestion() {
        viewModelScope.launch {
            if (_navigationState.value.currentQuestionIndex > 0) {
                _navigationState.value = _navigationState.value.copy(
                    currentQuestionIndex = _navigationState.value.currentQuestionIndex - 1
                )
            }
        }
    }

    fun moveToNextQuestion() {
        viewModelScope.launch {
            if (_navigationState.value.currentQuestionIndex < questions.size - 1) {
                _navigationState.value = _navigationState.value.copy(
                    currentQuestionIndex = _navigationState.value.currentQuestionIndex + 1
                )
            }
        }
    }


    fun completeCustomization() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCustomizationComplete = true)
        }
    }

    fun isCurrentQuestionAnswered(): Boolean {
        val currentQuestionIndex = _navigationState.value.currentQuestionIndex
        val currentQuestion = questions[currentQuestionIndex]
        return _uiState.value.selectedOptions.containsKey(currentQuestion.layerType)
    }

    fun restartCustomization() {
        viewModelScope.launch {
            _uiState.value = CustomizationState(selectedOptions = emptyMap(),customPerfumeName = "", isCustomizationComplete = false) // Reset UI state
            _navigationState.value = CustomizationNavigationState(currentScreen = CustomizationScreen.QUESTIONS,
                currentQuestionIndex = 0
            )
        }
    }

    fun navigateToNameInput() {
        viewModelScope.launch {
            _navigationState.value = _navigationState.value.copy(
                currentScreen = CustomizationScreen.NAME_INPUT
            )
        }
    }

    fun navigateToSummary() {
        viewModelScope.launch {
            _navigationState.value = _navigationState.value.copy(
                currentScreen = CustomizationScreen.SUMMARY
            )
        }
    }

    fun navigateToThankYou() {
        viewModelScope.launch {
            _navigationState.value = _navigationState.value.copy(
                currentScreen = CustomizationScreen.THANK_YOU
            )
        }
    }


    fun navigateBackToQuestions() {
        viewModelScope.launch {
            _navigationState.value = _navigationState.value.copy(
                currentScreen = CustomizationScreen.QUESTIONS,
                // Keep the current question index so user returns to where they were
                currentQuestionIndex = _navigationState.value.currentQuestionIndex
            )
        }
    }

    fun navigateBackToLastQuestion() {
        viewModelScope.launch {
            _navigationState.value = _navigationState.value.copy(
                currentScreen = CustomizationScreen.QUESTIONS,
                currentQuestionIndex = questions.size - 1 // Go to last question (index 2 for 3 questions)
            )
        }
    }

    fun showConfirmationDialog() {
        viewModelScope.launch {
            _showConfirmationDialog.value = true
        }
    }

    fun hideConfirmationDialog() {
        viewModelScope.launch {
            _showConfirmationDialog.value = false
        }
    }

    fun confirmAndNavigateToThankYou() {
        viewModelScope.launch {
            _showConfirmationDialog.value = false
            saveCustomizationToHistory(isAddedToCart = true)
            navigateToThankYou()
        }
    }

    fun getPerfumeName(): String {
        return _uiState.value.customPerfumeName.ifEmpty { "Custom Perfume" }
    }

    private fun saveCustomizationToHistory(isAddedToCart: Boolean) {
        if (customizationHistoryRepository == null || userId == null) return

        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val customization = CustomizationEntity(
                    userId = userId,
                    perfumeName = currentState.customPerfumeName.ifEmpty { "Custom Perfume" },
                    baseNote = getOptionName(currentState.selectedOptions[ScentLayer.BASE]),
                    essence = getOptionName(currentState.selectedOptions[ScentLayer.ESSENCE]),
                    experience = getOptionName(currentState.selectedOptions[ScentLayer.EXPERIENCE]),
                    createdAt = System.currentTimeMillis(),
                    isAddedToCart = isAddedToCart
                )

                customizationHistoryRepository.saveCustomization(customization)
                loadCustomizationHistory() // Refresh the history
            } catch (e: Exception) {
                println("Failed to save customization: ${e.message}")
            }
        }
    }

    private fun getOptionName(option: CustomizationOption?): String {
        return when (option?.textResId) {
            R.string.base_note_opt1 -> "Lime Basil & Mandarin"
            R.string.base_note_opt2 -> "Peony & Blush Suede"
            R.string.base_note_opt3 -> "English Pear & Freesia"
            R.string.base_note_opt4 -> "Wood Sage & Sea Salt"
            R.string.layering_opt1 -> "Nectarine Blossom & Honey"
            R.string.layering_opt2 -> "Orange Blossom"
            R.string.layering_opt3 -> "Tonka Bean & Vanilla"
            R.string.layering_opt4 -> "Red Roses"
            R.string.experience_opt1 -> "Daytime Elegance"
            R.string.experience_opt2 -> "Nighttime Allure"
            R.string.experience_opt3 -> "Special Gift"
            R.string.experience_opt4 -> "Self Love Ritual"
            else -> "Unknown"
        }
    }

    private fun loadCustomizationHistory() {
        if (customizationHistoryRepository == null || userId == null) return

        viewModelScope.launch {
            customizationHistoryRepository.getCustomizationsByUser(userId).collect { history ->
                _customizationHistory.value = history
            }
        }
    }

    fun completeCustomizationWithoutCart() {
        viewModelScope.launch {
            saveCustomizationToHistory(isAddedToCart = false)
        }
    }



}