package com.example.jomalonemobileapplication.feature.perfumeCustomization.data.repository

import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.feature.perfumeCustomization.domain.model.CustomizationOption
import com.example.jomalonemobileapplication.feature.perfumeCustomization.domain.model.CustomizationQuestion
import com.example.jomalonemobileapplication.feature.perfumeCustomization.domain.model.ScentLayer


object CustomizationRepository {
    fun getCustomizationQuestions(): List<CustomizationQuestion> {
            return listOf(
                CustomizationQuestion(
                    id = 1,
                    questionResId = R.string.base_note_question,
                    layerType = ScentLayer.BASE,
                    options = listOf(
                        CustomizationOption(
                            textResId = R.string.base_note_opt1,
                            imageResId = R.drawable.lime_basil,
                        ),
                        CustomizationOption(
                            textResId = R.string.base_note_opt2,
                            imageResId = R.drawable.peony
                        ),
                        CustomizationOption(
                            textResId = R.string.base_note_opt3,
                            imageResId = R.drawable.english_pear
                        ),
                        CustomizationOption(
                            textResId = R.string.base_note_opt4,
                            imageResId = R.drawable.wood_sage
                        )
                    )
                ),
                CustomizationQuestion(
                    id = 2,
                    questionResId = R.string.layering_question,
                    layerType = ScentLayer.ESSENCE,
                    options = listOf(
                        CustomizationOption(
                            textResId = R.string.layering_opt1,
                            imageResId = R.drawable.honey,
                        ),
                        CustomizationOption(
                            textResId = R.string.layering_opt2,
                            imageResId = R.drawable.orange_blossom
                        ),
                        CustomizationOption(
                            textResId = R.string.layering_opt3,
                            imageResId = R.drawable.tonka_bean
                        ),
                        CustomizationOption(
                            textResId = R.string.layering_opt4,
                            imageResId = R.drawable.red_roses
                        )
                    )
                ),
                CustomizationQuestion(
                    id = 3,
                    questionResId = R.string.experience_question,
                    layerType = ScentLayer.EXPERIENCE,
                    options = listOf(
                        CustomizationOption(
                            textResId = R.string.experience_opt1,
                            imageResId = R.drawable.day,
                        ),
                        CustomizationOption(
                            textResId = R.string.experience_opt2,
                            imageResId = R.drawable.night
                        ),
                        CustomizationOption(
                            textResId = R.string.experience_opt3,
                            imageResId = R.drawable.gift
                        ),
                        CustomizationOption(
                            textResId = R.string.experience_opt4,
                            imageResId = R.drawable.self_love
                        )
                    )
                )
            )
    }
}