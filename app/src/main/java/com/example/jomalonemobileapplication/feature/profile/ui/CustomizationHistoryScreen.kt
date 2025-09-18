package com.example.jomalonemobileapplication.feature.profile.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.AppDatabase
import com.example.jomalonemobileapplication.core.data.entity.CustomizationEntity
import com.example.jomalonemobileapplication.core.data.mapper.CartItemMapper
import com.example.jomalonemobileapplication.core.data.repository.CartRepositoryImpl
import com.example.jomalonemobileapplication.theme.*
import com.example.jomalonemobileapplication.core.ui.CartViewModel
import com.example.jomalonemobileapplication.core.ui.CartViewModelFactory
import com.example.jomalonemobileapplication.feature.perfumeCustomization.data.repository.CustomizationHistoryRepository
import kotlinx.coroutines.launch
import com.example.jomalonemobileapplication.theme.Background
import com.example.jomalonemobileapplication.theme.Cormorant
import com.example.jomalonemobileapplication.theme.Cream
import com.example.jomalonemobileapplication.theme.DarkBrown
import com.example.jomalonemobileapplication.theme.LightBrown
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationHistoryScreen(
    userId: String,
    onBack: () -> Unit
) {

    // local database
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { CustomizationHistoryRepository(database.customizationDao()) }

    val cartRepository = remember {
        CartRepositoryImpl(database.cartItemDao(), CartItemMapper())
    }
    val cartFactory = remember { CartViewModelFactory(cartRepository) }
    val cartViewModel: CartViewModel = viewModel(factory = cartFactory)

    val customizations by repository.getCustomizationsByUser(userId).collectAsStateWithLifecycle(initialValue = emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CustomizationEntity?>(null) }

    val coroutineScope = rememberCoroutineScope()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Added to Cart") },
            text = { Text("Your custom perfume has been successfully added to the cart!") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        refreshTrigger++
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBrown)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }

    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Customization") },
            text = { Text("Are you sure you want to delete \"${itemToDelete!!.perfumeName}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            repository.deleteCustomization(itemToDelete!!)
                            showDeleteDialog = false
                            itemToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF721C24))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text(
                        "Customization History",
                        fontFamily = Cormorant,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (customizations.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No Customizations Yet",
                            fontSize = 20.sp,
                            fontFamily = Cormorant,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start creating your custom perfumes!",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(customizations) { customization ->
                    CustomizationHistoryCard(
                        customization = customization,
                        onDeleteClick = {
                            itemToDelete = customization
                            showDeleteDialog = true
                        },
                        onAddToCartClick = {
                            cartViewModel.addCustomisedItemToCart(
                                name = customization.perfumeName,
                                size = "100ml",
                                unitPrice = 200.0,
                                quantity = 1
                            )

                            // Update the customization to mark as added to cart
                            coroutineScope.launch {
                                if (!customization.isAddedToCart) {
                                    val updatedCustomization = customization.copy(isAddedToCart = true)
                                    repository.updateCustomization(updatedCustomization)
                                }
                                showSuccessDialog = true
                            }
                        },
                        onShareClick = { shareCustomization(context, customization) }
                    )
                }
            }
        }
    }
}


@Composable
fun CustomizationHistoryCard(
    customization: CustomizationEntity,
    onDeleteClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onShareClick: () -> Unit

) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(customization.createdAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Cream)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = customization.perfumeName,
                    fontSize = 18.sp,
                    fontFamily = Cormorant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    if (customization.isAddedToCart) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkBrown)
                        ) {
                            Text(
                                text = "Added to Cart",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = DarkBrown,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Delete button
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFF721C24),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Created: $formattedDate",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Customization details
            CustomizationDetailRow("Base Note", customization.baseNote)
            CustomizationDetailRow("Essence", customization.essence)
            CustomizationDetailRow("Experience", customization.experience)

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddToCartClick,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(2.dp, DarkBrown),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightBrown,
                    contentColor = Color.Black
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Add to Cart - RM 200.00")
            }
        }
    }
}

fun shareCustomization(context: android.content.Context, customization: CustomizationEntity) {
    val shareText = buildString {
        appendLine("My Custom Jo Malone Perfume: ${customization.perfumeName}")
        appendLine()
        appendLine(" ${customization.perfumeName} ")
        appendLine()
        appendLine("🌿 Base Note: ${customization.baseNote}")
        appendLine("🌺 Essence: ${customization.essence}")
        appendLine("💫 Experience: ${customization.experience}")
        appendLine()
        appendLine("Created with Jo Malone London's customization experience!")
        appendLine("#JoMalone #CustomPerfume #Fragrance")
    }

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_SUBJECT, "Check out my custom Jo Malone perfume!")
    }

    val chooser = Intent.createChooser(shareIntent, "Share your custom perfume")
    context.startActivity(chooser)
}

@Composable
fun CustomizationDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Text(
            text = value,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomizationHistoryScreen() {
    CustomizationHistoryScreen(
        userId = "sampleUserId",
        onBack = {}
    )
}