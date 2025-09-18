package com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
fun PerfumeNameScreen(
    viewModel: CustomizationViewModel,
    perfumeName: String,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onNavigateToMain: () -> Unit

) {
    var currentName by remember { mutableStateOf(perfumeName) }

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
                        fontSize = 28.sp,
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
                    progress = { viewModel.getCurrentProgress() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.give_name_prompt),
                    fontSize = 30.sp,
                    fontFamily = Cormorant,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = currentName,
                    onValueChange = { currentName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    label = { Text("Perfume Name") },
                    placeholder = { Text("e.g. Midnight Blossom") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkBrown,
                        unfocusedBorderColor = LightBrown,
                        focusedTextColor = Color.Black
                    )
                )
            }

            item{
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.updatePerfumeName(currentName)
                            viewModel.navigateBackToLastQuestion()
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(2.dp, DarkBrown),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                    ) {
                        Text("Back to Questions")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            viewModel.updatePerfumeName(currentName)
                            onNext()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = currentName.isNotBlank(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightBrown,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Continue")
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfumeNameScreenPreview() {
    JoMaloneMobileApplicationTheme {
        val mockViewModel = CustomizationViewModel()
        PerfumeNameScreen(
            viewModel = mockViewModel,
            perfumeName = "",
            onNext = {},
            onBack = {},
            onNavigateToMain = {}
        )
    }
}