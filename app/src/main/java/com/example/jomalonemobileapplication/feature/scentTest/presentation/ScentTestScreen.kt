package com.example.jomalonemobileapplication.feature.scentTest.presentation
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentType


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.theme.Background
import com.example.jomalonemobileapplication.theme.Cormorant
import com.example.jomalonemobileapplication.theme.DarkBrown
import com.example.jomalonemobileapplication.theme.JoMaloneMobileApplicationTheme
import com.example.jomalonemobileapplication.theme.LightBrown
import com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation.ImageBasedQuestion
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.QuestionType
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentOption
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentQuestion
import com.example.jomalonemobileapplication.scentTest.data.repository.ScentTestRepository
import com.example.jomalonemobileapplication.feature.scentTest.presentation.ScentTestViewModel


@OptIn(ExperimentalMaterial3Api::class)    // for TopAppBar
@Composable
fun ScentTestScreen(
    modifier: Modifier = Modifier,
    viewModel: ScentTestViewModel = viewModel(),
    onTestComplete : (ScentType) -> Unit = {} ,
    onNavigateToMain: () -> Unit = { }

){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val questions = remember { ScentTestRepository.getQuestions()}


    LaunchedEffect(uiState.isTestCompleted) {
        if (uiState.isTestCompleted) {
            uiState.result?.let { result ->
                onTestComplete(result)
            }
        }
    }

    val currentQuestion = if (uiState.currentQuestionIndex in questions.indices) {
        questions[uiState.currentQuestionIndex]
    } else {
        LaunchedEffect(uiState.currentQuestionIndex) {
            viewModel.restartTest()
        }
        return
    }

//    val currentQuestion = questions[uiState.currentQuestionIndex]
    val isFirstQuestion = uiState.currentQuestionIndex == 0
    val isLastQuestion = uiState.currentQuestionIndex == questions.size -1
    val isCurrentQuestionAnswered = uiState.userSelections.containsKey(currentQuestion.id)

//    LaunchedEffect(uiState.isTestCompleted){
//        if (uiState.isTestCompleted){
//            uiState.result?.let { result ->
//                onTestComplete(result)
//            }
//        }
//    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateToMain) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to main")
                    }
                },
                title = {
                    Text(stringResource(R.string.scent_quiz_title),
                        fontFamily = Cormorant,
                        fontSize = 30.sp,
                        style = MaterialTheme.typography.headlineLarge,
//                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center)
                },
                modifier = modifier.padding(top = 20.dp, bottom = 20.dp),
                colors = TopAppBarDefaults.topAppBarColors( containerColor = Background)
            )
        },
        containerColor = Background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Add scroll capability
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = { (uiState.currentQuestionIndex + 1).toFloat() / questions.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Question content - this will expand to take available space
            QuestionComposables(
                question = currentQuestion,
                onOptionSelected = { selectedIndex ->
                    val selectedScentType = currentQuestion.options[selectedIndex].scentType
                    viewModel.selectOption(currentQuestion.id, selectedScentType)
                },
                modifier = Modifier.weight(1f, fill = false) // Don't force fill
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!isFirstQuestion) {
                    Button(
                        onClick = { viewModel.moveToPreviousQuestion() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightBrown,
                            contentColor = Color.Black
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(R.string.previous_button))
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Button(
                    onClick = {
                        if (isLastQuestion) {
                            viewModel.completeTest()
                        } else {
                            viewModel.moveToNextQuestion()
                        }
                    },
                    enabled = uiState.userSelections.containsKey(currentQuestion.id),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBrown,
                        contentColor = Color.Black
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(if (isLastQuestion) "Confirm" else stringResource(R.string.next_button))
                }
            }

            if (!isCurrentQuestionAnswered) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select an option to continue",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

//        Column(modifier = Modifier
//            .fillMaxSize()
//            .padding(innerPadding)
//            .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ){
//
//
//            // progress bar
//            LinearProgressIndicator(
//                progress = {(uiState.currentQuestionIndex + 1).toFloat() / questions.size},
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp),
//            )
//
//            QuestionComposables(
//                question = currentQuestion,
//                onOptionSelected = { selectedIndex ->
//                    val selectedScentType = currentQuestion.options[selectedIndex].scentType
//                    viewModel.selectOption(currentQuestion.id, selectedScentType)
//                },
//                modifier = Modifier.weight(1f)
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//
//
//            Row(
//                modifier = Modifier.fillMaxWidth().padding(16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ){
//                if (!isFirstQuestion){
//                    Button(
//                        onClick = { viewModel.moveToPreviousQuestion()},
//                        colors = ButtonDefaults.buttonColors(containerColor = LightBrown, contentColor = Color.Black),
//                        shape = MaterialTheme.shapes.medium,
//                    ){
//                        Text(stringResource(R.string.previous_button))
//                    }
//                } else {
//                    Spacer(modifier = Modifier.weight(1f))
//                }
//
//                Button(
//                    onClick = {
//                        if (isLastQuestion) {
//                            viewModel.completeTest()
//                        } else {
//                            viewModel.moveToNextQuestion()
//                        }
//                    },
//                    enabled = uiState.userSelections.containsKey(currentQuestion.id),
//                    colors = ButtonDefaults.buttonColors(containerColor = LightBrown, contentColor = Color.Black),
//                    shape = MaterialTheme.shapes.medium,
//
//                    ) {
//                    Text(if (isLastQuestion) "Confirm" else stringResource(R.string.next_button))
//                }
//            }
//            if (!isCurrentQuestionAnswered) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = "Select an option to continue",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
//                    fontStyle = FontStyle.Italic
//                )
//            }
//
//        }
//    }
//}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScentTestScreenPreview(){
    val viewModel = remember {ScentTestViewModel()}
    ScentTestScreen(viewModel = viewModel)

}