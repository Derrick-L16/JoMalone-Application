package com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.theme.Background
import com.example.jomalonemobileapplication.theme.Cormorant
import com.example.jomalonemobileapplication.theme.JoMaloneMobileApplicationTheme
import com.example.jomalonemobileapplication.feature.perfumeCustomization.data.repository.CustomizationRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfumeCustomizationScreen(
    modifier: Modifier = Modifier,
    viewModel: CustomizationViewModel,
    onNavigateToMain: () -> Unit,
    onComplete: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navState by viewModel.navigationState.collectAsStateWithLifecycle()
    val questions = CustomizationRepository.getCustomizationQuestions()
    val currentQuestionIndex = navState.currentQuestionIndex
    val currentQuestion = questions[currentQuestionIndex]
    val isFirstQuestion = currentQuestionIndex == 0
    val isLastQuestion = currentQuestionIndex == questions.size - 1

    val totalSteps = questions.size + 2 // questions + name input + summary
    val currentProgress = (currentQuestionIndex + 1).toFloat() / totalSteps

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateToMain) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to main"
                        )
                    }
                },
                title = {
                    Text(
                        stringResource(R.string.customization_title),
                        fontFamily = Cormorant,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                },
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                LinearProgressIndicator(
                    progress = { currentProgress},
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.message),
                    fontSize = 26.sp,
                    fontFamily = Cormorant,
                    textAlign = TextAlign.Center
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))

                ImageBasedQuestion(
                    question = currentQuestion,
                    onOptionSelected = { option ->
                        viewModel.selectOption(currentQuestion.layerType, option)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!isFirstQuestion) {
                        Button(
                            onClick = { viewModel.moveToPreviousQuestion() },
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text("Previous")
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Button(
                        onClick = {
                            if (isLastQuestion) {
                                viewModel.navigateToNameInput()
                            } else {
                                viewModel.moveToNextQuestion()
                            }
                        },
                        enabled = uiState.selectedOptions.containsKey(currentQuestion.layerType),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(if (isLastQuestion) "Complete" else "Next")
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun FragranceCustomizationScreenPreview() {
//    JoMaloneMobileApplicationTheme {
//        PerfumeCustomizationScreen()
//    }
//}
