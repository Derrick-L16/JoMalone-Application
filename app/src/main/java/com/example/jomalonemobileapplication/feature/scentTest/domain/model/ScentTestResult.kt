package com.example.jomalonemobileapplication.feature.scentTest.domain.model

class ScentTestResult(
    val scentType : ScentType,
    val description: String,
    val recommendedPerfumes: List<String>
)

object ScentResultRepository{
    fun getResultDescription(scentType: ScentType): ScentTestResult {
        return when (scentType) {
            ScentType.CITRUS -> ScentTestResult(scentType = ScentType.CITRUS, description = "You have a vibrant and energetic personality, much like the fresh and zesty notes of citrus scents. You enjoy lively environments and are often the life of the party.", recommendedPerfumes = listOf("Jo Malone Lime Basil & Mandarin", "Jo Malone Grapefruit"))
            ScentType.FLORAL -> ScentTestResult(scentType = ScentType.FLORAL, description = "You possess a gentle and romantic nature, akin to the delicate and enchanting aroma of floral fragrances. You appreciate beauty in all forms and have a nurturing spirit.", recommendedPerfumes = listOf("Jo Malone Peony & Blush Suede", "Jo Malone Red Roses"))
            ScentType.WOODY -> ScentTestResult(scentType = ScentType.WOODY, description = "You exude strength and reliability, similar to the grounding and earthy qualities of woody scents. You are dependable, practical, and have a deep connection to nature.", recommendedPerfumes = listOf("Jo MaloneWood Sage & Sea Salt", "Jo Malone Oud & Bergamot"))
            ScentType.SPICY -> ScentTestResult(scentType = ScentType.SPICY, description = "You are bold and adventurous, reflecting the warm and invigorating essence of spicy fragrances. You thrive on excitement and are always ready to take on new challenges.", recommendedPerfumes = listOf("Jo Malone Pomegranate Noir", "Jo Malone Dark Amber & Ginger Lily"))
        }

    }
}