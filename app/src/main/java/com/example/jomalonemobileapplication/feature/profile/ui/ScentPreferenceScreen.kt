package com.example.jomalonemobileapplication.feature.profile.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jomalonemobileapplication.theme.Cormorant
import com.example.jomalonemobileapplication.feature.login.ui.AuthViewModel
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentType
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentResultRepository
import com.example.jomalonemobileapplication.theme.Background
import com.example.jomalonemobileapplication.theme.DarkBrown
import com.example.jomalonemobileapplication.theme.LightBrown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScentPreferenceScreen(
    onBack: () -> Unit,
    onNavigateToScentTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AuthViewModel = viewModel()
    val profileState by viewModel.userProfileState.collectAsState()

    var showClearConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
        viewModel.loadUserScentPreference()
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Clear Scent Preference") },
            text = { Text("Are you sure you want to clear your scent preference? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearConfirmation = false
                        viewModel.clearUserScentPreference {
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF721C24))
                ) {
                    Text("Clear", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Scent Preference",
                        fontFamily = Cormorant,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors( containerColor = Background)
            )
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (profileState.scentPreference != null) {
                val scentType = try {
                    ScentType.valueOf(profileState.scentPreference!!)
                } catch (e: IllegalArgumentException) {
                    null
                }

                if (scentType != null) {
                    val result = ScentResultRepository.getResultDescription(scentType)

                    Text(
                        text = "Your Current Scent Preference",
                        fontFamily = Cormorant,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = scentType.getDisplayName(),
                        fontFamily = Cormorant,
                        style = MaterialTheme.typography.headlineMedium,
                        color = DarkBrown,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = result.description,
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 16.dp)
                    )

                    Text(
                        text = "Recommended for you:",
                        fontFamily = Cormorant,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    result.recommendedPerfumes.forEach { perfume ->
                        Text(
                            text = "• $perfume",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                } else {
                    Text("Invalid scent preference data")
                }
            } else {
                Text(
                    text = "You haven't taken the scent test yet!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNavigateToScentTest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(width = 2.dp, color = DarkBrown, shape = MaterialTheme.shapes.medium),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = LightBrown, contentColor = Color.Black)
            ) {
                Text(
                    text = if (profileState.scentPreference != null)
                        "Retake Scent Test"
                    else
                        "Take Scent Test"
                )
            }

            if (profileState.scentPreference != null) {
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = { showClearConfirmation = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear Preference", color = DarkBrown)
                }
            }
        }
    }
}