package com.example.jomalonemobileapplication.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.AppDatabase
import com.example.jomalonemobileapplication.core.data.entity.CartItem
import com.example.jomalonemobileapplication.core.data.entity.OrderEntity
import com.example.jomalonemobileapplication.core.data.entity.PaymentMethod
import com.example.jomalonemobileapplication.core.data.entity.PaymentTypes
import com.example.jomalonemobileapplication.core.data.mapper.PaymentMethodMapper
import com.example.jomalonemobileapplication.core.data.repository.OrderRepository
import com.example.jomalonemobileapplication.core.data.repository.PaymentRepository
import com.example.jomalonemobileapplication.theme.Background
import com.example.jomalonemobileapplication.theme.Cream
import com.example.jomalonemobileapplication.theme.DarkBrown
import com.example.jomalonemobileapplication.theme.JoMaloneMobileApplicationTheme
import com.example.jomalonemobileapplication.feature.login.ui.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*

data class OrderWithDetails(
    val order: OrderEntity,
    val paymentMethod: PaymentMethod,
    val items: List<CartItem>
)

@Composable
fun PaymentHistoryScreen(
    uiState: PaymentHistoryUiState,
    onCancel: (String) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier.background(Background)
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filterOptions = listOf("All", "In Progress", "Shipped", "Delivered", "Cancelled")

    // Filter orders based on selected status
    val filteredOrders = when (selectedFilter) {
        "All" -> uiState.orders
        "In Progress" -> uiState.orders.filter {
            it.order.deliveryStatus.equals("Pending", true) ||
                    it.order.deliveryStatus.equals("Confirmed", true) ||
                    it.order.deliveryStatus.equals("Processing", true)
        }
        "Shipped" -> uiState.orders.filter {
            it.order.deliveryStatus.equals("Shipped", true)
        }
        "Delivered" -> uiState.orders.filter {
            it.order.deliveryStatus.equals("Delivered", true)
        }
        "Cancelled" -> uiState.orders.filter {
            it.order.deliveryStatus.equals("Cancelled", true)
        }
        else -> uiState.orders
    }


    // Show error dialog
    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { onClearError() },
            title = { Text("Error") },
            text = { Text(uiState.error) },
            confirmButton = {
                TextButton(onClick = { onClearError() }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        Text(
            text = "Payment History",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Filter tabs
        FilterTabs(
            selectedFilter = selectedFilter,
            filterOptions = filterOptions,
            onFilterSelected = { selectedFilter = it }
        )

        // Content
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (filteredOrders.isEmpty()) {
            EmptyHistoryState(
                selectedFilter = selectedFilter,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders) { orderWithDetails ->
                    PaymentHistoryCard(
                        orderWithDetails = orderWithDetails,
                        onCancel = onCancel
                    )
                }
            }
        }
    }
}

@Composable
fun FilterTabs(
    selectedFilter: String,
    filterOptions: List<String>,
    onFilterSelected: (String) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = filterOptions.indexOf(selectedFilter),
        modifier = Modifier.fillMaxWidth(),
        containerColor = Background,
        contentColor = DarkBrown,
        edgePadding = 16.dp
    ) {
        filterOptions.forEach { option ->
            Tab(
                selected = selectedFilter == option,
                onClick = { onFilterSelected(option) },
                text = {
                    Text(
                        text = option,
                        fontSize = 14.sp,
                        fontWeight = if (selectedFilter == option) FontWeight.Bold else FontWeight.Medium
                    )
                }
            )
        }
    }
}

@Composable
fun PaymentHistoryCard(
    orderWithDetails: OrderWithDetails,
    onCancel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val order = orderWithDetails.order
    val paymentMethod = orderWithDetails.paymentMethod
    val items = orderWithDetails.items

    var showCancelDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Cream)
    ) {

        Column( modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp) ) {
            // Header row with order info and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.orderId}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatDate(order.orderDate),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                OrderStatusChip(status = order.deliveryStatus) }
            // Items preview (show first 2 items)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items.take(2).forEach { item ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = item.name,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = "${item.name} (${item.quantity}x)",
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (items.size > 2) {
                    Text(
                        text = "and ${items.size - 2} more items",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            HorizontalDivider()

            // Payment and total info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        PaymentTypes.getIcon(paymentMethod.paymentType),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = DarkBrown
                    )
                    Column {
                        Text(
                            text = PaymentTypes.getDisplayName(paymentMethod.paymentType),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        when (paymentMethod.paymentType) {
                            PaymentTypes.CREDIT_CARD ->
                                { paymentMethod.lastFourDigits?.let { lastFour ->
                                    Text(
                                        text = "**** $lastFour",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    ) }
                                }
                            PaymentTypes.TNG_E_WALLET ->
                                { paymentMethod.phoneNumber?.let { phone ->
                                    Text(
                                        text = phone,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    ) }
                                }
                            else -> { /* No additional details needed */ }
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "RM ${String.format("%.2f", order.total)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )
                }
            }

            // Cancel button
            if (!order.deliveryStatus.equals("Cancelled", true)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { showCancelDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("Cancel", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // Cancel order confirmation dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Order") },
            text = { Text("Are you sure you want to cancel this order?") },
            confirmButton = {
                TextButton(onClick = {
                    onCancel(order.orderId)
                    showCancelDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}


@Composable
fun FeedbackDialog(
    title: String,
    message: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var feedback by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(title) },
        text = {
            Column {
                Text(message)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    placeholder = { Text("Enter feedback...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(feedback) }) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun OrderStatusChip(status: String) {
    val (backgroundColor, contentColor, icon) = when (status) {
        "Pending" -> Triple(
            Color(0xFFFFF3CD),
            Color(0xFF856404),
            Icons.Default.Pending
        )
        "Confirmed" -> Triple(
            Color(0xFFD4EDDA),
            Color(0xFF155724),
            Icons.Default.Check
        )
        "Shipped" -> Triple(
            Color(0xFFD1ECF1),
            Color(0xFF0C5460),
            Icons.Default.LocalShipping
        )
        "Delivered" -> Triple(
            Color(0xFFD4EDDA),
            Color(0xFF155724),
            Icons.Default.CheckCircle
        )
        "Cancelled" -> Triple(
            Color(0xFFF8D7DA),
            Color(0xFF721C24),
            Icons.Default.Cancel
        )
        else -> Triple(
            Color(0xFFF8D7DA),
            Color(0xFF721C24),
            Icons.Default.Cancel
        )
    }

    Row(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = contentColor
        )
        Text(
            text = status,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}

@Composable
fun EmptyHistoryState(
    selectedFilter: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CreditCard,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (selectedFilter == "All") {
                "No Payment History"
            } else {
                "No $selectedFilter Orders"
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Text(
            text = if (selectedFilter == "All") {
                "Your payment history will appear here after you make your first purchase."
            } else {
                "You don't have any $selectedFilter orders yet."
            },
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

// Utility function to format date
private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
    return formatter.format(date)
}

// Sample data for preview
fun getSamplePaymentHistoryUiState(): PaymentHistoryUiState {
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
            isSelected = true,
            unitPrice = 120.00
        )
    )

    return PaymentHistoryUiState(
        orders = listOf(
            OrderWithDetails(
                order = OrderEntity(
                    orderId = "12345",
                    userId = "1",
                    orderDate = System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000), // 1 day ago
                    deliveryStatus = "Delivered",
                    subTotal = 320.00,
                    tax = 19.20,
                    estimatedDelivery = 15.00,
                    total = 354.20,
                    paymentMethodId = 1,
                    deliveryAddressId = 1,
                    orderItems = emptyList()
                ),
                paymentMethod = PaymentMethod(
                    id = 1,
                    userId = "1",
                    paymentType = PaymentTypes.CREDIT_CARD,
                    details = mapOf("lastFourDigits" to "1234"),
                    isSelected = false
                ),
                items = sampleItems
            ),
            OrderWithDetails(
                order = OrderEntity(
                    orderId = "12344",
                    userId = "1",
                    orderDate = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000), // 3 days ago
                    deliveryStatus = "Shipped",
                    subTotal = 200.00,
                    tax = 12.00,
                    estimatedDelivery = 15.00,
                    total = 227.00,
                    paymentMethodId = 2,
                    deliveryAddressId = 1,
                    orderItems = emptyList()
                ),
                paymentMethod = PaymentMethod(
                    id = 2,
                    userId = "1",
                    paymentType = PaymentTypes.TNG_E_WALLET,
                    details = mapOf("phoneNumber" to "0123456789"),
                    isSelected = false
                ),
                items = listOf(sampleItems.first())
            ),
            OrderWithDetails(
                order = OrderEntity(
                    orderId = "12343",
                    userId = "1",
                    orderDate = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000), // 7 days ago
                    deliveryStatus = "Cancelled",
                    subTotal = 150.00,
                    tax = 9.00,
                    estimatedDelivery = 15.00,
                    total = 174.00,
                    paymentMethodId = 3,
                    deliveryAddressId = 1,
                    orderItems = emptyList()
                ),
                paymentMethod = PaymentMethod(
                    id = 3,
                    userId = "1",
                    paymentType = PaymentTypes.CASH_ON_DELIVERY,
                    details = emptyMap(),
                    isSelected = false
                ),
                items = listOf(sampleItems.first())
            )
        ),
        isLoading = false,
        error = null
    )
}

@Composable
fun PaymentHistoryRoute(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }

    val orderRepository = remember(database) {
        OrderRepository(database.orderDao())
    }
    val paymentRepository = remember(database) {
        PaymentRepository(database.paymentMethodDao(), PaymentMethodMapper())
    }

    val viewModel: PaymentHistoryViewModel = viewModel(
        factory = PaymentHistoryViewModelFactory(orderRepository, authViewModel, paymentRepository)
    )

    val uiState by viewModel.uiState.collectAsState()

    PaymentHistoryScreen(
        uiState = uiState,
        onCancel = { orderId -> viewModel.cancelOrder(orderId) },
        onClearError = { viewModel.clearError() },
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun PaymentHistoryScreenPreview() {
    JoMaloneMobileApplicationTheme {
        PaymentHistoryScreen(
            uiState = getSamplePaymentHistoryUiState(),
            onCancel = {},
            onClearError = {}
        )
    }
}