package com.example.jomalonemobileapplication.feature.scentTest.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.theme.Background
import com.example.jomalonemobileapplication.theme.Cormorant
import com.example.jomalonemobileapplication.theme.DarkBrown
import com.example.jomalonemobileapplication.theme.JoMaloneMobileApplicationTheme
import com.example.jomalonemobileapplication.theme.LightBrown
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentTestResult
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScentResultScreen(
    modifier : Modifier = Modifier,
    result : ScentTestResult,
    onCustomizeClick: () -> Unit,
    onRetakeTest: () -> Unit,
    onGoToMain: () -> Unit,
    onSavePreference: (ScentType) -> Unit = {}

) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(stringResource(R.string.match_summary_title),
                    fontFamily = Cormorant,
                    fontSize = 28.sp,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold)
            },
                colors = TopAppBarDefaults.topAppBarColors( containerColor = Background)
            )
        },
        modifier = modifier,
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
                Text(
                    text = stringResource(R.string.signature_scent_title),
                    fontFamily = Cormorant,
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.scentType.getDisplayName(),
                    fontFamily = Cormorant,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Spacer(modifier = Modifier.padding(16.dp))
                Text(text = result.description,
                    fontFamily = Cormorant,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(56.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.recommended_text),
                        fontFamily = Cormorant,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(26.dp))
                result.recommendedPerfumes.forEach { perfume ->

                    Text(
                        text = "• $perfume",
                        fontSize = 24.sp,
                        fontFamily = Cormorant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = {
                        onGoToMain()
                              },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(width = 2.dp, color = DarkBrown, shape = MaterialTheme.shapes.medium),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = LightBrown, contentColor = Color.Black)
                ) {
                    Text(
                        text = stringResource(R.string.go_main),
                        fontSize = 16.sp
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onCustomizeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(width = 2.dp, color = DarkBrown, shape = MaterialTheme.shapes.medium),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = LightBrown, contentColor = Color.Black)
                ) {
                    Text(
                        text = stringResource(R.string.customize_button),
                        fontSize = 16.sp
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onRetakeTest,
                    border = BorderStroke(2.dp, DarkBrown),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = stringResource(R.string.retake_quiz_button),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScentResultScreenFloralPreview() {
    JoMaloneMobileApplicationTheme {
        ScentResultScreen(
            result = ScentTestResult(
                scentType = ScentType.FLORAL,
                description = "The romantic, soft and elegant scent that makes you feel beautiful and graceful",
                recommendedPerfumes = listOf("Peony & Blush Suede")
            ),
            onCustomizeClick = {},
            onRetakeTest = {},
            onGoToMain = {}
        )
    }
}
