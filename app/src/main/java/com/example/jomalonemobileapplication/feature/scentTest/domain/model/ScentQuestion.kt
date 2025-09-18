package com.example.jomalonemobileapplication.feature.scentTest.domain.model
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.QuestionType

data class ScentQuestion(
    val id: Int,
    val titleResId: Int?,    // For section titles like R.string.mood_title (can int or null)
    val questionResId: Int,  // R.string.question1
    val questionType: QuestionType,
    val options: List<ScentOption>,
    val imageRedIds: List<Int>? = null
)

data class ScentOption (
    val textResId : Int,
    val scentType : ScentType,
    val imageResId: Int? = null
)