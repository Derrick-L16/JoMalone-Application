package com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.theme.Background
import com.example.jomalonemobileapplication.theme.Cormorant
import com.example.jomalonemobileapplication.theme.DarkBrown
import com.example.jomalonemobileapplication.theme.JoMaloneMobileApplicationTheme
import com.example.jomalonemobileapplication.theme.LightBrown
import com.example.jomalonemobileapplication.core.ui.CartViewModel
import com.example.jomalonemobileapplication.feature.perfumeCustomization.domain.model.ScentLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationSummaryScreen(
    viewModel: CustomizationViewModel,
    onConfirm: () -> Unit,
    onAddToCart: () -> Unit,
    onEdit: () -> Unit,
    onNavigateToMain : () -> Unit

) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showDialog by viewModel.showConfirmationDialog.collectAsStateWithLifecycle()

    if (showDialog) {
        ConfirmationDialog(
            onConfirm = {
                onAddToCart()
                onConfirm()
            },
            onDismiss = {
                viewModel.hideConfirmationDialog()
                viewModel.completeCustomizationWithoutCart()
                onNavigateToMain()
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.customization_title),
                        fontFamily = Cormorant,
                        fontSize = 30.sp,
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
                    progress = { 0.9f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.review_message),
                    fontSize = 30.sp,
                    fontFamily = Cormorant,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = LightBrown)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = uiState.customPerfumeName.ifEmpty { "Your Custom Perfume" },
                            fontSize = 24.sp,
                            fontFamily = Cormorant,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Display selected options
                        uiState.selectedOptions.forEach { (layer, option) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = when (layer) {
                                        ScentLayer.BASE -> "Base Note:"
                                        ScentLayer.ESSENCE -> "Essence:"
                                        ScentLayer.EXPERIENCE -> "Experience:"
                                    },
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(text = stringResource(option.textResId))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                    }
                }
            }
            
            item{
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Fixed for every customized perfume:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Show price
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Price:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "RM 200.00 (100ml)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(2.dp, DarkBrown),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text("Back to Edit")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { viewModel.showConfirmationDialog()

                                  // add to cart
                                  },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(2.dp, DarkBrown),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightBrown,
                            contentColor = Color.Black
                        ),
                        shape = MaterialTheme.shapes.medium,

                        ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CustomizationSummaryScreenPreview() {
    JoMaloneMobileApplicationTheme {
        val mockViewModel = CustomizationViewModel()
        CustomizationSummaryScreen(
            viewModel = mockViewModel,
            onConfirm = {},
            onEdit = {},
            onAddToCart = {},
            onNavigateToMain = {}
        )
    }
}