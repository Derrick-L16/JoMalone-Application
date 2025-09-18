package com.example.jomalonemobileapplication.feature.perfumeCustomization.domain.model

enum class ScentLayer {
    BASE, ESSENCE, EXPERIENCE
}

data class CustomizationOption(
    val textResId: Int,
    val imageResId : Int,
)

data class CustomizationQuestion(
    val id: Int,
    val questionResId: Int,
    val layerType: ScentLayer,
    val options: List<CustomizationOption>
)


