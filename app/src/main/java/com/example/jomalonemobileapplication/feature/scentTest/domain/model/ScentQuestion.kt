package com.example.jomalonemobileapplication.feature.scentTest.domain.model

data class ScentQuestion(
    val id: Int,
    val titleResId: Int?,
    val questionResId: Int,
    val questionType: QuestionType,
    val options: List<ScentOption>,
    val imageRedIds: List<Int>? = null
)

data class ScentOption (
    val textResId : Int,
    val scentType : ScentType,
    val imageResId: Int? = null
)