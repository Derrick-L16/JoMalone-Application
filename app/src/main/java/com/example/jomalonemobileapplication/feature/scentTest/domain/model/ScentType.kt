package com.example.jomalonemobileapplication.feature.scentTest.domain.model

enum class ScentType {
    CITRUS,
    FLORAL,
    WOODY,
    SPICY;

    fun getDisplayName(): String {
        return when (this) {
            CITRUS -> "Citrus"
            FLORAL -> "Floral"
            WOODY -> "Woody"
            SPICY -> "Spicy"
        }
    }
}