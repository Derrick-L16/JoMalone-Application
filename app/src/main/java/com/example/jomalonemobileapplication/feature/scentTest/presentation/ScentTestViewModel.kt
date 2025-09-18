package com.example.jomalonemobileapplication.feature.scentTest.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentType
import com.example.jomalonemobileapplication.scentTest.data.repository.ScentTestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class Screen {
    object ScentTest : Screen()
    data class ScentResult(val result: ScentType): Screen()
    object FragranceCustomization : Screen()
    object CustomizationResult : Screen()
}

data class ScentTestState(
    val currentQuestionIndex : Int = 0,
    val userSelections: Map<Int, ScentType> = emptyMap(), // track which scent type was chosen for each question
    val isTestCompleted: Boolean = false,
    val result: ScentType? = null
){
    fun addSelection(questionId: Int, scentType: ScentType) : ScentTestState{
        return copy(userSelections = userSelections + (questionId to scentType))
    }

    fun moveToNextQuestion(): ScentTestState {
        return copy(currentQuestionIndex = currentQuestionIndex + 1)
    }

    fun isCurrentQuestionAnswered(questionId: Int): Boolean {
        return userSelections.containsKey(questionId)
    }

    fun completeTest(finalResult: ScentType): ScentTestState {
        return copy(isTestCompleted = true, result = finalResult)
    }
}
class ScentTestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScentTestState())
    val uiState: StateFlow<ScentTestState> = _uiState.asStateFlow()

    private val questions = ScentTestRepository.getQuestions()

    fun selectOption(questionId: Int, scentType: ScentType){
        viewModelScope.launch {
            val currentState = _uiState.value
            val newSelections = currentState.userSelections + (questionId to scentType)

            _uiState.value = currentState.copy(userSelections = newSelections)


//            val newState = if (currentState.currentQuestionIndex < questions.size -1) {
//                currentState.copy(userSelections = newSelections, currentQuestionIndex = currentState.currentQuestionIndex + 1)
//            } else {
//                val result = calculateResult(newSelections)
//                currentState.copy(userSelections = newSelections, isTestCompleted = true, result = result)
//            }
//            _uiState.value = newState
        }
    }

    fun moveToPreviousQuestion(){
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.currentQuestionIndex > 0) {
                _uiState.value = currentState.copy(
                    currentQuestionIndex = currentState.currentQuestionIndex - 1
                )
            }
        }
    }

    fun moveToNextQuestion() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.currentQuestionIndex < questions.size - 1){
                _uiState.value = currentState.copy(
                    currentQuestionIndex = currentState.currentQuestionIndex + 1)
            }
        }
    }

    fun completeTest() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val result = calculateResult(currentState.userSelections)
            _uiState.value = currentState.copy(
                isTestCompleted = true,
                result = result
            )
        }
    }

//    private fun calculateResult(selections: Map<Int, ScentType>): ScentType {
//        // count how frequently each scent type was chosen
//        val scentCount = selections.values.groupingBy {it}.eachCount()
//
//        return scentCount.maxByOrNull {it.value}?.key ?: ScentType.CITRUS  // default to CITRUS if no selections
//
//    }
    private fun calculateResult(selections: Map<Int, ScentType>): ScentType {
        val scentCount = selections.values.groupingBy { it }.eachCount()
        val maxCount = scentCount.maxOfOrNull { it.value } ?: return ScentType.CITRUS

        // Get all types with the maximum count
        val topTypes = scentCount.filter { it.value == maxCount }.keys

        // If there's a tie, use a predefined priority order
        return if (topTypes.size > 1) {
            // Define your tie-breaking priority here
            val priorityOrder = listOf(ScentType.FLORAL, ScentType.CITRUS, ScentType.WOODY, ScentType.SPICY)
            priorityOrder.firstOrNull { it in topTypes } ?: topTypes.first()
        } else {
            topTypes.first()
        }
    }

    fun restartTest() {
        viewModelScope.launch{
            _uiState.value = ScentTestState()
        }
    }
}