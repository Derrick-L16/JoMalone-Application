package com.example.jomalonemobileapplication.scentTest.data.repository

import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.QuestionType
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentOption
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentQuestion
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentType

object ScentTestRepository {
    fun getQuestions(): List<ScentQuestion> {
        return listOf(
            ScentQuestion(
                id = 1,
                titleResId = R.string.mood_title,
                questionResId = R.string.question1,
                questionType = QuestionType.BUTTON_BASED,
                options = listOf(
                    ScentOption(R.string.q1_opt1, ScentType.CITRUS),
                    ScentOption(R.string.q1_opt2, ScentType.FLORAL),
                    ScentOption(R.string.q1_opt3, ScentType.WOODY),
                    ScentOption(R.string.q1_opt4, ScentType.SPICY),
            )
        ),
            ScentQuestion(
                id = 2,
                titleResId = null,
                questionResId = R.string.question2,
                questionType = QuestionType.BUTTON_BASED,
                options = listOf(
                    ScentOption(R.string.q2_opt1, ScentType.CITRUS),
                    ScentOption(R.string.q2_opt2, ScentType.FLORAL),
                    ScentOption(R.string.q2_opt3, ScentType.WOODY),
                    ScentOption(R.string.q2_opt4, ScentType.SPICY),
                )
            ),

            ScentQuestion(
                id = 3,
                titleResId = null,
                questionResId = R.string.question3,
                questionType = QuestionType.IMAGE_BUTTON,
                options = listOf(
                    // dunno if this is a bad practice （set textResId to null）
                    ScentOption(R.string.garden_text, ScentType.CITRUS, R.drawable.garden),
                    ScentOption(R.string.beach_text, ScentType.FLORAL, R.drawable.beach),
                    ScentOption(R.string.roof_top_text, ScentType.WOODY, R.drawable.rooftop),
                    ScentOption(R.string.cafe_text, ScentType.SPICY, R.drawable.cafe),
                )
            ),
            ScentQuestion(
                id = 4,
                titleResId = null,
                questionResId = R.string.question4,
                questionType = QuestionType.BUTTON_BASED,
                options = listOf(
                    ScentOption(R.string.q4_opt1, ScentType.CITRUS),
                    ScentOption(R.string.q4_opt2, ScentType.FLORAL),
                    ScentOption(R.string.q4_opt3, ScentType.WOODY),
                    ScentOption(R.string.q4_opt4, ScentType.SPICY),
                )
            ),
            ScentQuestion(
                id = 5,
                titleResId = R.string.preference_title,
                questionResId = R.string.question5,
                questionType = QuestionType.BUTTON_BASED,
                options = listOf(
                    ScentOption(R.string.q5_opt1, ScentType.CITRUS),
                    ScentOption(R.string.q5_opt2, ScentType.FLORAL),
                    ScentOption(R.string.q5_opt3, ScentType.WOODY),
                    ScentOption(R.string.q5_opt4, ScentType.SPICY),
                )
            ),
            ScentQuestion(
                id = 6,
                titleResId = null,
                questionResId = R.string.question6,
                questionType = QuestionType.IMAGE_BUTTON,
                options = listOf(
                    // dunno if this is a bad practice （set textResId to null）
                    ScentOption(R.string.pink_color, ScentType.CITRUS, R.drawable.pink),
                    ScentOption(R.string.blue_color, ScentType.FLORAL, R.drawable.blue),
                    ScentOption(R.string.gold_color, ScentType.WOODY, R.drawable.gold),
                    ScentOption(R.string.green_color, ScentType.SPICY, R.drawable.green),
                )
            ),
            ScentQuestion(
                id = 7,
                titleResId = R.string.occasion_title,
                questionResId = R.string.question7,
                questionType = QuestionType.BUTTON_BASED,
                options = listOf(
                    ScentOption(R.string.q7_opt1, ScentType.CITRUS),
                    ScentOption(R.string.q7_opt2, ScentType.FLORAL),
                    ScentOption(R.string.q7_opt3, ScentType.WOODY),
                    ScentOption(R.string.q7_opt4, ScentType.SPICY),
                )
            ),
            ScentQuestion(
                id = 8,
                titleResId = null,
                questionResId = R.string.question8,
                questionType = QuestionType.BUTTON_BASED,
                options = listOf(
                    ScentOption(R.string.q8_opt1, ScentType.CITRUS),
                    ScentOption(R.string.q8_opt2, ScentType.FLORAL),
                    ScentOption(R.string.q8_opt3, ScentType.WOODY),
                    ScentOption(R.string.q8_opt4, ScentType.SPICY),
                )
            ),
        )
    }
}