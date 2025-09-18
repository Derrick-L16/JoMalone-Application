package com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.theme.Background
import com.example.jomalonemobileapplication.theme.Cormorant
import com.example.jomalonemobileapplication.theme.DarkBrown
import com.example.jomalonemobileapplication.theme.JoMaloneMobileApplicationTheme
import com.example.jomalonemobileapplication.theme.LightBrown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThankYouScreen(
    perfumeName: String,
    onComplete: () -> Unit
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.customization_title),
                        fontFamily = Cormorant,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
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
                    progress = { 1.0f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = stringResource(R.string.thank_you),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontFamily = Cormorant,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Your custom perfume '$perfumeName' has been created successfully!",
                    fontSize = 24.sp,
                    fontFamily = Cormorant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = stringResource(R.string.prepare_message),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = stringResource(R.string.appreciate_message),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
            item {
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 32.dp)
                        .border(width = 2.dp, color = DarkBrown, shape = MaterialTheme.shapes.medium),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBrown,
                        contentColor = Color.Black
                    ),
                    shape = MaterialTheme.shapes.medium,

                    ) {
                    Text("Back to Home", fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ThankYouScreenPreview() {
    JoMaloneMobileApplicationTheme {
        ThankYouScreen(
            perfumeName = "Midnight Blossom",
            onComplete = {}
        )
    }
}