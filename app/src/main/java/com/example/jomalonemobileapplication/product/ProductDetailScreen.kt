package com.example.jomalonemobileapplication.product

import com.example.jomalonemobileapplication.R
import android.widget.Toast // Import Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite // For filled wishlist icon
import androidx.compose.material.icons.filled.FavoriteBorder // For bordered wishlist icon
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jomalonemobileapplication.data.Perfume
import com.example.jomalonemobileapplication.feature.login.ui.MADAppTheme // Assuming your theme is defined


// Color scheme (ensure these are defined or imported from your theme)
val CreamBackground = Color(0xFFFAF7F2)
val DarkBrown = Color(0xFF2C1810)
val LightGray = Color(0xFF9E9E9E) // Consider using MaterialTheme.colorScheme.onSurfaceVariant
val StarGold = Color(0xFFFFC107)
val ButtonBlack = Color(0xFF1A1A1A)
val WishlistRed = Color(0xFFE53935) // Example color for a filled wishlist icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    perfumeId: String?,
    onNavigateBack: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel() // Inject ViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current // Get context for Toast

    // Trigger loading when perfumeId is available and not already loading/loaded for this ID
    LaunchedEffect(perfumeId) { // Simplified key
        if (perfumeId != null && perfumeId != uiState.perfume?.id) { // Load if ID changes or perfume is null
            viewModel.loadPerfume(perfumeId)
        } else if (perfumeId != null && uiState.perfume == null && !uiState.isLoading) { // Initial load if not already loading
            viewModel.loadPerfume(perfumeId)
        }
    }

    // --- LaunchedEffects to show messages from ViewModel ---
    LaunchedEffect(uiState.wishlistMessage) {
        uiState.wishlistMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearWishlistMessage() // Clear the message after showing
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMessage ->
            val isLoadingErrorDisplayed = uiState.perfume == null && !uiState.isLoading
            // Show toast only if it's not the main loading error OR if it's a specific action error
            if (!isLoadingErrorDisplayed || errorMessage.contains("wishlist", ignoreCase = true) || errorMessage.contains("preferences", ignoreCase = true)) {
                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
            }
            viewModel.clearError()
        }
    }
    // --- END of LaunchedEffects for messages ---

    Scaffold(
        containerColor = CreamBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.perfume?.name ?: "Product Details",
                        color = DarkBrown,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkBrown
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle menu (e.g., share, report) */ }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options", // Corrected content description
                            tint = DarkBrown
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = CreamBackground.copy(alpha = 0.9f)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading && uiState.perfume == null -> {
                    CircularProgressIndicator(color = DarkBrown)
                }
                uiState.error != null && uiState.perfume == null -> { // Shows error only if perfume hasn't loaded
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                // perfumeId should still be available if we got to this state with an error after trying to load
                                viewModel.refreshPerfume() // Use refresh function
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkBrown)
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
                uiState.perfume != null -> {
                    PerfumeDetailsContent(
                        perfume = uiState.perfume!!,
                        viewModel = viewModel,
                        isCurrentlyInWishlist = uiState.isInWishlist // <-- Pass from ViewModel's state
                    )
                }
                perfumeId == null && !uiState.isLoading -> {
                    Text(
                        "No product ID provided.",
                        textAlign = TextAlign.Center,
                        color = DarkBrown,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> { // Fallback, e.g., brief moment before loading starts
                    if (!uiState.isLoading) { // Don't show if already showing loader
                        Text("Initializing product details...", textAlign = TextAlign.Center, color = DarkBrown)
                    } else {
                        CircularProgressIndicator(color = DarkBrown) // Should be caught by the first condition if perfume is null
                    }
                }
            }
        }
    }
}

@Composable
private fun PerfumeDetailsContent(
    perfume: Perfume,
    viewModel: ProductDetailViewModel,
    isCurrentlyInWishlist: Boolean // Receive wishlist status from the ViewModel's uiState
) {
    val scrollState = rememberScrollState()
    // Local state 'localIsInWishlist' is REMOVED.
    // The UI will directly react to 'isCurrentlyInWishlist' from the ViewModel.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 88.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp), // Height for the image container
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(perfume.imageUrl ?: R.drawable.ic_placeholder_perfume)
                    .crossfade(true)
                    .error(R.drawable.ic_placeholder_perfume)
                    .placeholder(R.drawable.ic_placeholder_perfume)
                    .build(),
                contentDescription = perfume.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth(0.7f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!perfume.imageUrl.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                Card(
                    modifier = Modifier.size(64.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AsyncImage(
                        model = perfume.imageUrl,
                        contentDescription = "Product thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                repeat(2) {
                    Card(
                        modifier = Modifier.size(64.dp),
                        colors = CardDefaults.cardColors(containerColor = LightGray.copy(alpha = 0.2f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) { Box(modifier = Modifier.fillMaxSize()) }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = perfume.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = DarkBrown,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(4) { Icon(Icons.Default.Star, contentDescription = "Star", tint = StarGold, modifier = Modifier.size(20.dp)) }
            Icon(Icons.Default.Star, contentDescription = "Star (empty)", tint = LightGray.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("4.0 (123 reviews)", style = MaterialTheme.typography.bodyMedium, color = LightGray)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "RM ${String.format(if (perfume.price % 1.0 == 0.0) "%.0f" else "%.2f", perfume.price)}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBrown
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (perfume.stockQuantity > 0) "${perfume.stockQuantity} in stock" else "Out of stock",
            style = MaterialTheme.typography.bodyMedium,
            color = if (perfume.stockQuantity > 0) DarkBrown.copy(alpha = 0.8f) else WishlistRed.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.toggleWishlistAndUpdatePreferences() // Call the ViewModel's toggle function
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isCurrentlyInWishlist) WishlistRed else DarkBrown // Use state from ViewModel
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.5.dp,
                    brush = SolidColor(if (isCurrentlyInWishlist) WishlistRed else DarkBrown) // Use state from ViewModel
                )
            ) {
                Icon(
                    imageVector = if (isCurrentlyInWishlist) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, // Use state from ViewModel
                    contentDescription = "Wishlist",
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Wishlist", style = MaterialTheme.typography.labelLarge)
            }
            Button(
                onClick = { /* TODO: Handle add to cart */ },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBlack,
                    contentColor = Color.White
                ),
                enabled = perfume.stockQuantity > 0
            ) {
                Text("Add to Cart", style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Product Description Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )
            Spacer(modifier = Modifier.height(8.dp))

        }
        // Removed extra spacer from here as bottom padding is on the main Column
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFAF7F2)
@Composable
fun ProductDetailScreenContentPreview() {
    MADAppTheme {
        val samplePerfume = Perfume(
            id = "sample1",
            name = "Enchanté Bloom Deluxe",
            stockQuantity = 15,
            price = 280.00,
            imageUrl = null,
            tastes = listOf("Floral", "Fruity", "Fresh"),
            capacity = 100,
        )
        // For a more useful preview of ProductDetailScreen, you'd need a fake ViewModel
        // that provides a sample uiState.
        ProductDetailScreen(
            perfumeId = "sample1",
            onNavigateBack = {},
            // viewModel = fakeViewModel // You would create a fake ViewModel for previews
            // Using hiltViewModel() in preview will provide a stub.
            viewModel = hiltViewModel<ProductDetailViewModel>()
        )
        // To directly preview PerfumeDetailsContent with a specific wishlist state:
        // PerfumeDetailsContent(
        //     perfume = samplePerfume,
        //     viewModel = hiltViewModel(), // Stub ViewModel
        //     isCurrentlyInWishlist = true // or false to see the difference
        // )
    }
}
