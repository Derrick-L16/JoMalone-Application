package com.example.jomalonemobileapplication.mainPage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.core.data.entity.CartItem
import com.example.jomalonemobileapplication.data.Perfume

// import coil.compose.AsyncImage // Uncomment if you use Coil for image loading
import java.util.UUID // CORRECT IMPORT FOR UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoMaloneMainPage(
    onNavigateToShoppingCart: () -> Unit = {},
    navController: NavController? = null,
    onAddToCart: (CartItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Sample Perfume data with more items to ensure scrolling
    val sampleItems = getSampleCartItems()
    val fragrantFavourites = List(10) { index ->
        Perfume(
            id = UUID.randomUUID().toString(),
            name = "Raspberry Ripple Cologne ${index + 1}",
            stockQuantity = 10 + index,
            price = 500.00 + (index * 10),
            tastes = listOf("Fruity", "Sweet", "Creamy"),
            imageUrl = null,
            capacity = 100
        )
    }

    val woodyScents = List(10) { index ->
        Perfume(
            id = UUID.randomUUID().toString(),
            name = "Pomegranate Noir Cologne ${index + 1}",
            stockQuantity = 12 + index,
            price = 520.00 + (index * 5),
            tastes = listOf("Woody", "Fruity", "Spicy"),
            imageUrl = null,
            capacity = 100
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6F0)) // Consider MaterialTheme.colorScheme.background
    ) {
        // Header
        item {
            TopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "JO MALONE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black, // Consider MaterialTheme.colorScheme.onSurface
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "LONDON",
                            fontSize = 12.sp,
                            color = Color.Black, // Consider MaterialTheme.colorScheme.onSurface
                            letterSpacing = 3.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Handle menu click */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.Black // Consider MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle search click */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Black // Consider MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onNavigateToShoppingCart) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Shopping Cart",
                            tint = Color.Black // Consider MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F6F0) // Consider MaterialTheme.colorScheme.surface
                )
            )
        }

        // Fragrant Favourites Section
        item {
            SectionHeader("Fragrant Favourites")
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val sampleItems = getSampleCartItems()

                items(sampleItems) { cartItem ->
                    Button(
                        onClick = { onAddToCart(cartItem) }, // ✅ pass the CartItem
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.width(200.dp) // keep consistent size
                    ) {
                        Text(
                            text = "Add ${cartItem.name} (RM ${"%.2f".format(cartItem.unitPrice)})",
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }



        item {
            Spacer(modifier = Modifier.height(32.dp))
        }

        // From Your Favourite Scent - Woody Section
        item {
            SectionHeader("From Your Favourite Scent - Woody")
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(woodyScents, key = { perfume -> perfume.id }) { perfume ->
                    PerfumeCard(perfume = perfume) {
                        // TODO: Handle perfume click
                        navController?.navigate("perfumeDetail/${perfume.id}") // Example navigation
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Discover Our Services Section
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black, // Consider MaterialTheme.colorScheme.onSurfaceVariant
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun AddToCartButtons(items: List<CartItem>, onAddToCart: (CartItem) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items.forEach { cartItem ->
            Button(
                onClick = { onAddToCart(cartItem) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Add ${cartItem.name} to Cart (RM ${"%.2f".format(cartItem.unitPrice)})",
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun PerfumeCard(
    perfume: Perfume,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Consider MaterialTheme.colorScheme.surfaceVariant or surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (perfume.imageUrl != null) {
                    // Example with Coil (add Coil dependency if not present: implementation("io.coil-kt:coil-compose:2.7.0"))
                    // AsyncImage(
                    // model = perfume.imageUrl,
                    // contentDescription = perfume.name,
                    // modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                    // contentScale = ContentScale.Crop,
                    // error = painterResource(id = R.drawable.ic_placeholder_perfume), // Optional placeholder
                    // placeholder = painterResource(id = R.drawable.ic_loading_perfume) // Optional loading
                    // )
                    Text("IMG", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                } else {
                    Icon(
                        imageVector = Icons.Default.LocalFlorist,
                        contentDescription = perfume.name,
                        modifier = Modifier.size(50.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = perfume.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                maxLines = 2,
                modifier = Modifier.heightIn(min = 32.dp),
                color = Color.DarkGray
            )

            Text(
                text = "${perfume.capacity} ml",
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = "RM ${"%.2f".format(perfume.price)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp)
            )

            perfume.tastes?.take(1)?.joinToString()?.let {
                Text(
                    text = "Note: $it",
                    fontSize = 9.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}



@Composable
fun ServiceButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = Color.DarkGray
        )
    }
}

fun getSampleCartItems(): List<CartItem> {
    return listOf(
        CartItem(
            cartItemId = 1,
            productId = 1,
            name = "Raspberry Ripple Cologne",
            size = "100 ml",
            imageRes = R.drawable.raspberry,
            quantity = 2,
            isSelected = true,
            unitPrice = 100.00
        ),
        CartItem(
            cartItemId = 2,
            productId = 2,
            name = "Orange Blossom Cologne",
            size = "100 ml",
            imageRes = R.drawable.orange,
            quantity = 1,
            isSelected = false,
            unitPrice = 120.00
        ),
        CartItem(
            cartItemId = 3,
            productId = 3,
            name = "Peony & Blush Suede",
            size = "50 ml",
            imageRes = R.drawable.peony,
            quantity = 3,
            isSelected = true,
            unitPrice = 80.00
        )
    )
}


@Preview(showBackground = true, name = "Jo Malone Main Page Preview")
@Composable
fun JoMaloneMainPagePreview() {
    // If you have a theme for MADApp (e.g., MADAppTheme from your ui.theme package),
    // it's highly recommended to wrap your preview in it:
    // MaterialTheme { // Or YourMADAppTheme
    JoMaloneMainPage()
    // }
}

