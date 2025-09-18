//package com.example.jomalonemobileapplication.navigation
//
//import com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation.CustomizationFlow
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.jomalonemobileapplication.core.ui.CartViewModel
//import com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation.CustomizationViewModel
//import com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation.PerfumeCustomizationScreen
//import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentResultRepository
//import com.example.jomalonemobileapplication.feature.scentTest.presentation.ScentTestScreen
//import com.example.jomalonemobileapplication.feature.scentTest.presentation.ScentResultScreen
//import com.example.jomalonemobileapplication.feature.scentTest.presentation.ScentTestViewModel
//import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentType
//
//
//
//@Composable
//fun TestScreenNavigation() {
//    var currentScreen by remember { mutableStateOf("ScentTest") }
//    var testResult by remember { mutableStateOf<ScentType?>(null) }
//
//    val scentTestViewModel: ScentTestViewModel = viewModel()
//    val customizationViewModel: CustomizationViewModel = viewModel()
////    val cartViewModel: CartViewModel = viewModel() //
//
//    LaunchedEffect(currentScreen) {
//        if (currentScreen == "PerfumeCustomization") {
//            customizationViewModel.restartCustomization()
////            customizationViewModel.resetCompletion()
//
//        }
//    }
//
////    // Monitor customization completion
////    val customizationState by customizationViewModel.uiState.collectAsStateWithLifecycle()
////    LaunchedEffect(customizationState.isCustomizationComplete) {
////        if (customizationState.isCustomizationComplete) {
////
////            currentScreen = "ScentTest"
////            customizationViewModel.restartCustomization()
////            scentTestViewModel.restartTest()
////        }
////    }
//
//    when (currentScreen) {
//        "ScentTest" -> {
//            ScentTestScreen(
//                viewModel = scentTestViewModel,
//                onTestComplete = { result ->
//                    testResult = result
//                    currentScreen = "ScentResult"
//                }
//            )
//        }
//        "ScentResult" -> {
//            val scentTypeResult = testResult ?: ScentType.CITRUS
//            val scentTestResult = ScentResultRepository.getResultDescription(scentTypeResult)
//
//            ScentResultScreen(
//                result = scentTestResult,
//                onCustomizeClick = {
//                    currentScreen = "PerfumeCustomization"
//                    customizationViewModel.restartCustomization()
//                },
//                onRetakeTest = {
//                    currentScreen = "ScentTest"
//                    scentTestViewModel.restartTest()
//                },
//                onGoToMain = {}
//            )
//        }
//        "PerfumeCustomization" -> {
//            CustomizationFlow(
//                viewModel = customizationViewModel,
////                cartViewModel = cartViewModel,
//                onComplete = {
//                    currentScreen = "ScentTest"
//                    customizationViewModel.restartCustomization()
//                    scentTestViewModel.restartTest()
//                }
//            )
//        }
//    }
//}