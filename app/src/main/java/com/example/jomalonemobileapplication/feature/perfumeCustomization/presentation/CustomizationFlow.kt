package com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jomalonemobileapplication.AppDatabase
import com.example.jomalonemobileapplication.theme.JoMaloneMobileApplicationTheme
import com.example.jomalonemobileapplication.core.ui.CartViewModel
import com.example.jomalonemobileapplication.feature.perfumeCustomization.data.repository.CustomizationHistoryRepository
import com.example.jomalonemobileapplication.feature.perfumeCustomization.data.repository.CustomizationRepository


@SuppressLint("RememberReturnType")
@Composable
fun CustomizationFlow(
//    viewModel: CustomizationViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    userId: String?,
    onComplete: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val customizationHistoryRepository = remember {
        CustomizationHistoryRepository(database.customizationDao())
    }

    val viewModel = remember {
        CustomizationViewModel(customizationHistoryRepository, userId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navState by viewModel.navigationState.collectAsStateWithLifecycle()
    val questions = remember { CustomizationRepository.getCustomizationQuestions() }
    val isLastQuestion = navState.currentQuestionIndex == questions.size - 1 //

//    LaunchedEffect(navState.currentScreen) {
//        if (navState.currentScreen == CustomizationScreen.THANK_YOU) {
//            onComplete()
//        }
//    }

    when (navState.currentScreen) {
        CustomizationScreen.QUESTIONS -> {
            PerfumeCustomizationScreen(
                viewModel = viewModel,
                onNavigateToMain = onNavigateToMain,
                onComplete = {
                    if (isLastQuestion) {
                        viewModel.navigateToNameInput()
                    } else {
                        viewModel.moveToNextQuestion()
                    }
                }
            )
        }
        CustomizationScreen.NAME_INPUT -> {
            PerfumeNameScreen(
                viewModel = viewModel,
                perfumeName = uiState.customPerfumeName,
                onNext = { viewModel.navigateToSummary() },
                onBack = { viewModel.navigateBackToQuestions() },
                onNavigateToMain = onNavigateToMain,
            )
        }
        CustomizationScreen.SUMMARY -> {
            CustomizationSummaryScreen(
                viewModel = viewModel,
                onConfirm = {
                    viewModel.confirmAndNavigateToThankYou()
                },
                onEdit = { viewModel.navigateToNameInput() },
                onAddToCart = {
                    val perfumeName = viewModel.getPerfumeName()

                    cartViewModel.addCustomisedItemToCart(
                        name = perfumeName,
                        size = "100ml",
                        unitPrice = 200.0,
                        quantity = 1
                    )
                },
                onNavigateToMain = onNavigateToMain,
            )
        }
        CustomizationScreen.THANK_YOU -> {
            ThankYouScreen(
                perfumeName = uiState.customPerfumeName,
                onComplete = {
                    viewModel.completeCustomization()
                    onComplete() }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CustomizationFlowPreview() {
    JoMaloneMobileApplicationTheme {
        CustomizationFlow(onComplete = {},
            userId = "sampleUserId",
            onNavigateToMain = {}
        )
    }
}