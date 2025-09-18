package com.example.jomalonemobileapplication.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.AppDatabase
import com.example.jomalonemobileapplication.theme.Background
import com.example.jomalonemobileapplication.theme.Cream
import com.example.jomalonemobileapplication.theme.JoMaloneMobileApplicationTheme
import com.example.jomalonemobileapplication.core.data.mapper.CartItemMapper
import com.example.jomalonemobileapplication.core.data.repository.CartRepositoryImpl
import com.example.jomalonemobileapplication.core.data.entity.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCartScreen(
    uiState: CartUiState,
    onItemClick: (CartItem) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onIncreaseQuantity: (CartItem) -> Unit,
    onDecreaseQuantity: (CartItem) -> Unit,
    onSelectItem: (Int, Boolean) -> Unit,
    onSelectAll: (Boolean) -> Unit,
    onProceedButtonClicked: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .background(Background)
            .padding(4.dp)
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), // tighter padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Shopping Bag",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = "COMPLIMENTARY GIFT WRAP WITH EVERY ORDER",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Empty State
        if (uiState.isEmpty) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your cart is empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Add some items to get started",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            // Product List
            LazyColumn(
                modifier = modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.items, key = { it.cartItemId }) { item ->
                    CartItemRow(
                        item = item,
                        isSelected = uiState.selectedIds.contains(item.cartItemId),
                        onCloseClick = { onRemoveItem(item) },
                        onIncreaseClick = { onIncreaseQuantity(item) },
                        onDecreaseClick = { onDecreaseQuantity(item) },
                        onCheckedChange = { selected ->
                            onSelectItem(item.cartItemId, selected)
                        },
                        onItemClick = onItemClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        BottomSection(
            uiState = uiState,
            onSelectAll = onSelectAll,
            onProceedButtonClicked = onProceedButtonClicked
        )
    }
}




@Composable
fun ShoppingCartRoute(
    onNavigateToCheckout: () -> Unit,
    onItemClick: (CartItem) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    useSampleData: Boolean = false
) {
    if (useSampleData) {
        val sampleUiState = getSampleCartUiState()
        ShoppingCartScreen(
            uiState = sampleUiState,
            onItemClick = {},
            onRemoveItem = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onSelectItem = { _, _ -> },
            onSelectAll = {},
            onProceedButtonClicked = onNavigateToCheckout,
            onBack = onBack,
            modifier = modifier
        )
    } else {
        val context = LocalContext.current
        val database = remember { AppDatabase.getDatabase(context) }

        val repository = remember(database) {
            CartRepositoryImpl(database.cartItemDao(), CartItemMapper())
        }

        val viewModel: CartViewModel = viewModel(
            factory = CartViewModelFactory(repository)
        )

        val uiState by viewModel.uiState.collectAsState()

        ShoppingCartScreen(
            uiState = uiState,
            onRemoveItem = viewModel::removeItem,
            onIncreaseQuantity = viewModel::increaseQuantity,
            onDecreaseQuantity = viewModel::decreaseQuantity,
            onSelectItem = viewModel::selectCartItem,
            onSelectAll = viewModel::selectAll,
            onProceedButtonClicked = onNavigateToCheckout,
            onBack = onBack,
            onItemClick = onItemClick,
            modifier = modifier
        )
    }
}

// Sample data for Shopping Cart
fun getSampleCartUiState(): CartUiState {
    val sampleItems = listOf(
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
            imageRes = R.drawable.peony, // make sure you have peony drawable
            quantity = 3,
            isSelected = true,
            unitPrice = 80.00
        )
    )

    return CartUiState(
        items = sampleItems,
        selectedIds = sampleItems.filter { it.isSelected }.map { it.cartItemId }.toSet()
    )
}


@Composable
fun CartItemRow(
    item: CartItem,
    isSelected: Boolean = false,
    onCloseClick: () -> Unit = {},
    onIncreaseClick: () -> Unit = {},
    onDecreaseClick: () -> Unit = {},
    onCheckedChange: (Boolean) -> Unit = {},
    onItemClick: (CartItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Cream),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onItemClick(item) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF8B4513),
                    uncheckedColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Product image
            ProductImageWithResourceId(
                imageRes = item.imageRes.toString(),
                productName = item.name
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Product Details
            Column(modifier = Modifier.weight(1f)) {
                // Product name and remove button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(0xFFFFF7CE), shape = CircleShape)
                            .clickable { onCloseClick() }
                            .border(1.dp, Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove item",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                    }


                }

                Text(
                    text = "Size: ${item.size}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.padding(4.dp))

                // Change quantity
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDecreaseClick,
                            modifier = Modifier.size(24.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White
                            ),
                        ) {
                            Text(
                                text = "-",
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }

                        Text(
                            text = item.quantity.toString(),
                            modifier = Modifier
                                .width(32.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )

                        OutlinedButton(
                            onClick = onIncreaseClick,
                            modifier = Modifier.size(24.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White
                            ),
                        ) {
                            Text(
                                text = "+",
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "RM %.2f".format(item.unitPrice),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Text(
                            text = "RM %.2f".format(item.totalPrice),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomSection(
    uiState: CartUiState,
    onSelectAll: (Boolean) -> Unit,
    onProceedButtonClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(16.dp)
    ) {
        // Select All Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Cream),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.isAllSelected,
                        onCheckedChange = onSelectAll,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF8B4513)
                        )
                    )
                    Text("Select All", fontSize = 14.sp)
                }

                Text(
                    text = "Total: RM %.2f".format(uiState.total),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Payment Button
        Button(
            onClick = onProceedButtonClicked,
            enabled = !uiState.isEmpty && uiState.selectedItems.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (uiState.selectedItems.isEmpty()) {
                    "Select items to checkout"
                } else {
                    "Proceed To Payment"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProductImageWithResourceId(
    imageRes: String,
    productName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(70.dp)
            .background(Cream)
            .border(1.dp, Color.Gray.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {

        val resourceId = imageRes.toIntOrNull()
        if (resourceId != null && resourceId != 0) {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = productName,
                modifier = Modifier.size(70.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            ProductImagePlaceholder()
        }
    }
}

// Empty placeholder with background
@Composable
private fun ProductImagePlaceholder() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Cream)
    )
}

@Preview(showBackground = true)
@Composable
fun ShoppingCartScreenPreview() {
    JoMaloneMobileApplicationTheme {
        val sampleUiState = getSampleCartUiState()
        ShoppingCartScreen(
            uiState = sampleUiState,
            onItemClick = { },
            onRemoveItem = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onSelectItem = { _, _ -> },
            onSelectAll = {},
            onProceedButtonClicked = {},
            onBack = {}
        )
    }
}

