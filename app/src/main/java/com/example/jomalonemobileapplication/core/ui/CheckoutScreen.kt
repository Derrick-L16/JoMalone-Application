package com.example.jomalonemobileapplication.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.AppDatabase
import com.example.jomalonemobileapplication.core.data.entity.CartItem
import com.example.jomalonemobileapplication.core.data.entity.DeliveryAddress
import com.example.jomalonemobileapplication.core.data.entity.Order
import com.example.jomalonemobileapplication.core.data.entity.PaymentMethod
import com.example.jomalonemobileapplication.core.data.entity.PaymentTypes
import com.example.jomalonemobileapplication.core.data.mapper.CartItemMapper
import com.example.jomalonemobileapplication.core.data.mapper.DeliveryAddressMapper
import com.example.jomalonemobileapplication.core.data.mapper.PaymentMethodMapper
import com.example.jomalonemobileapplication.core.data.repository.AddressRepository
import com.example.jomalonemobileapplication.core.data.repository.CartRepositoryImpl
import com.example.jomalonemobileapplication.core.data.repository.CheckoutRepository
import com.example.jomalonemobileapplication.core.data.repository.OrderRepository
import com.example.jomalonemobileapplication.core.data.repository.PaymentRepository
import com.example.jomalonemobileapplication.theme.Background
import com.example.jomalonemobileapplication.theme.Cream
import com.example.jomalonemobileapplication.theme.DarkBrown
import com.example.jomalonemobileapplication.theme.JoMaloneMobileApplicationTheme
import com.example.jomalonemobileapplication.feature.login.ui.AuthViewModel

@Composable
fun CheckoutScreen(
    uiState: CheckoutUiState,
    onCheckoutSuccess: (orderNumber: String) -> Unit,
    onSelectPaymentMethod: (Int) -> Unit,
    onSetDefaultAddress: (Int) -> Unit,
    onProcessCheckout: (Int, Int) -> Unit,
    onAddDeliveryAddress: (DeliveryAddress) -> Unit,
    onAddPaymentMethod: (PaymentMethod) -> Unit,
    onDeleteDeliveryAddress: (DeliveryAddress) -> Unit,
    onDeletePaymentMethod: (PaymentMethod) -> Unit,
    onClearError: () -> Unit,
    onClearCheckoutSuccess: () -> Unit,
    modifier: Modifier = Modifier.background(Background)
) {

    var showAddAddressDialog by remember { mutableStateOf(false) }
    var showAddPaymentDialog by remember { mutableStateOf(false) }

    val canProceedCheckout = remember(uiState) {
        val hasValidPayment = uiState.selectedPaymentMethod != null
        val hasValidAddress = uiState.defaultAddress != null
        val hasCartItems = uiState.cartItems.isNotEmpty()
        val totalIsValid = uiState.order.total > 0

        hasValidPayment && hasValidAddress && hasCartItems && totalIsValid && !uiState.isLoading
    }

    var checkoutError by remember { mutableStateOf<String?>(null) }

    // Handle checkout success
    LaunchedEffect(uiState.checkoutSuccess) {
        uiState.checkoutSuccess?.let { orderId ->
            onCheckoutSuccess(uiState.order.orderId)
            onClearCheckoutSuccess()
        }
    }


    // Add Address Dialog
    if (showAddAddressDialog) {
        AddAddressDialog(
            onDismiss = { showAddAddressDialog = false },
            onAddAddress = { address ->
                onAddDeliveryAddress(address)
                showAddAddressDialog = false
            }
        )
    }

    // Add Payment Method Dialog
    if (showAddPaymentDialog) {
        AddPaymentMethodDialog(
            onDismiss = { showAddPaymentDialog = false },
            onAddPaymentMethod = { paymentMethod ->
                onAddPaymentMethod(paymentMethod)
                showAddPaymentDialog = false
            }
        )
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

    // Content
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Header
        item {
            Text(
                text = "Checkout",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Order Items Section
        item {
            CheckoutSection(title = "Order Items") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    uiState.cartItems.forEach { cartItem ->
                        CartItemCard(cartItem = cartItem)
                    }
                }
            }
        }

        // Delivery Address Section
        item {
            CheckoutSectionWithAdd(
                title = "Delivery Address",
                onAddClick = { showAddAddressDialog = true }
            ) {
                if (uiState.deliveryAddresses.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.deliveryAddresses.forEach { address ->
                            AddressCard(
                                address = address,
                                isSelected = address.id == uiState.defaultAddress?.id,
                                onSelect = {
                                    onSetDefaultAddress(address.id)
                                },
                                onDelete = {
                                    onDeleteDeliveryAddress(address)
                                },
                                showDelete = uiState.deliveryAddresses.size > 1,

                                )
                        }
                    }
                } else {
                    EmptyStateCard(
                        icon = Icons.Default.LocationOn,
                        text = "No delivery addresses available\nTap + to add one"
                    )
                }
            }
        }

        // Payment Method Section
        item {
            CheckoutSectionWithAdd(
                title = "Payment Method",
                onAddClick = { showAddPaymentDialog = true }
            ) {
                if (!uiState.paymentMethods.isNullOrEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.paymentMethods.forEach { payment ->
                            PaymentMethodCard(
                                paymentMethod = payment,
                                isSelected = payment.id == uiState.selectedPaymentMethod?.id,
                                onSelect = {
                                    onSelectPaymentMethod(payment.id)
                                },
                                onDelete = {
                                    onDeletePaymentMethod(payment)
                                },
                                showDelete = uiState.paymentMethods.size > 1
                            )
                        }
                    }
                } else {
                    EmptyStateCard(
                        icon = Icons.Default.CreditCard,
                        text = "No payment methods available\nTap + to add one"
                    )
                }
            }
        }

        // Order Summary Section
        item {
            CheckoutSection(title = "Order Summary") {
                OrderSummaryCard(
                    subtotal = uiState.order.subTotal,
                    deliveryFee = uiState.order.estimatedDelivery,
                    tax = uiState.order.tax,
                    total = uiState.order.total
                )
            }
        }

        // Checkout Button
        item {
            Button(
                onClick = {
                    try {
                        when {
                            uiState.cartItems.isEmpty() -> {
                                checkoutError = "Your cart is empty"
                            }
                            uiState.defaultAddress == null -> {
                                checkoutError = "Please select a delivery address"
                            }
                            uiState.selectedPaymentMethod == null -> {
                                checkoutError = "Please select a payment method"
                            }
                            uiState.order.total <= 0 -> {
                                checkoutError = "Invalid order total"
                            }
                            else -> {
                                onProcessCheckout(
                                    uiState.selectedPaymentMethod!!.id,
                                    uiState.defaultAddress!!.id
                                )
                            }
                        }
                    } catch (e: Exception) {
                        checkoutError = "Failed to process checkout. Please try again."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = canProceedCheckout,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null)
                        Text(
                            text = "Complete Order - RM ${String.format("%.2f", uiState.order.total)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
    // Show checkout error
    checkoutError?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or dialog
            checkoutError = null
        }
    }
}

@Composable
fun CheckoutSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Cream)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun CheckoutSectionWithAdd(
    title: String,
    onAddClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(
                onClick = onAddClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add $title",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Cream)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun AddAddressDialog(
    onDismiss: () -> Unit,
    onAddAddress: (DeliveryAddress) -> Unit
) {
    var addressName by remember { mutableStateOf("") }
    var addressLine by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }

    // Error states
    var addressNameError by remember { mutableStateOf<String?>(null) }
    var addressLineError by remember { mutableStateOf<String?>(null) }
    var cityError by remember { mutableStateOf<String?>(null) }
    var stateError by remember { mutableStateOf<String?>(null) }
    var postalCodeError by remember { mutableStateOf<String?>(null) }

    // Validation functions
    fun validateAddressName(value: String): String? {
        return when {
            value.isBlank() -> "Address name is required"
            value.length < 2 -> "Address name must be at least 2 characters"
            value.length > 50 -> "Address name must be less than 50 characters"
            else -> null
        }
    }

    fun validateAddressLine(value: String): String? {
        return when {
            value.isBlank() -> "Address line is required"
            value.length < 5 -> "Address must be at least 5 characters"
            value.length > 100 -> "Address must be less than 100 characters"
            else -> null
        }
    }

    fun validateCity(value: String): String? {
        return when {
            value.isBlank() -> "City is required"
            !value.matches(Regex("^[a-zA-Z\\s]+$")) -> "City should only contain letters"
            value.length < 2 -> "City must be at least 2 characters"
            else -> null
        }
    }

    fun validateState(value: String): String? {
        val malaysianStates = listOf(
            "Johor", "Kedah", "Kelantan", "Malacca", "Negeri Sembilan",
            "Pahang", "Penang", "Perak", "Perlis", "Sabah", "Sarawak",
            "Selangor", "Terengganu", "Kuala Lumpur", "Labuan", "Putrajaya"
        )
        return when {
            value.isBlank() -> "State is required"
            !malaysianStates.any { it.equals(value, ignoreCase = true) } ->
                "Please enter a valid Malaysian state"
            else -> null
        }
    }

    fun validatePostalCode(value: String): String? {
        return when {
            value.isBlank() -> "Postal code is required"
            !value.matches(Regex("^\\d{5}$")) -> "Postal code must be 5 digits"
            else -> null
        }
    }

    fun validateAllFields(): Boolean {
        addressNameError = validateAddressName(addressName)
        addressLineError = validateAddressLine(addressLine)
        cityError = validateCity(city)
        stateError = validateState(state)
        postalCodeError = validatePostalCode(postalCode)

        return listOf(addressNameError, addressLineError, cityError, stateError, postalCodeError)
            .all { it == null }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Cream)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add New Address",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = addressName,
                    onValueChange = { addressName = it },
                    label = { Text("Address Name (e.g., Home, Work)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = addressNameError != null,
                    supportingText = {
                        addressNameError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                )

                OutlinedTextField(
                    value = addressLine,
                    onValueChange = {
                        addressLine = it
                        addressLineError = null
                    },
                    label = { Text("Address Line") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    isError = addressLineError != null,
                    supportingText = {
                        addressLineError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = {
                            city = it
                            cityError = null
                        },
                        label = { Text("City") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = cityError != null,
                        supportingText = {
                            cityError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = postalCode,
                        onValueChange = {
                            if (it.length <= 5 && it.all { char -> char.isDigit() }) {
                                postalCode = it
                                postalCodeError = null
                            }
                        },
                        label = { Text("Postal Code") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = postalCodeError != null,
                        supportingText = {
                            postalCodeError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    )
                }

                OutlinedTextField(
                    value = state,
                    onValueChange = {
                        state = it
                        stateError = null
                    },
                    label = { Text("State") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = stateError != null,
                    supportingText = {
                        stateError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (validateAllFields()) {
                                try {
                                    val newAddress = DeliveryAddress(
                                        id = 0,
                                        name = addressName.trim(),
                                        userId = "1", // Should come from auth
                                        addressLine = addressLine.trim(),
                                        city = city.trim(),
                                        state = state.trim(),
                                        postalCode = postalCode.trim(),
                                        isDefault = false
                                    )
                                    onAddAddress(newAddress)
                                } catch (e: Exception) {
                                    // Handle creation error
                                    addressNameError = "Failed to create address. Please try again."
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentMethodDialog(
    onDismiss: () -> Unit,
    onAddPaymentMethod: (PaymentMethod) -> Unit
) {
    var selectedPaymentType by remember { mutableStateOf(PaymentTypes.CREDIT_CARD) }
    var expanded by remember { mutableStateOf(false) }

    // Form fields
    var cardNumber by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Cream)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Payment Method",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Payment Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = PaymentTypes.getDisplayName(selectedPaymentType),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Type") },
                        leadingIcon = {
                            Icon(PaymentTypes.getIcon(selectedPaymentType), contentDescription = null)
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        PaymentTypes.ALL_TYPES.forEach { paymentType ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(PaymentTypes.getIcon(paymentType), contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(PaymentTypes.getDisplayName(paymentType))
                                    }
                                },
                                onClick = {
                                    selectedPaymentType = paymentType
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Dynamic form
                when (selectedPaymentType) {
                    PaymentTypes.CREDIT_CARD -> {
                        CreditCardForm(
                            cardNumber = cardNumber,
                            onCardNumberChange = { cardNumber = it },
                            cardHolderName = cardHolderName,
                            onCardHolderNameChange = { cardHolderName = it },
                            expiryDate = expiryDate,
                            onExpiryDateChange = { expiryDate = it },
                            cvv = cvv,
                            onCvvChange = { cvv = it }
                        )
                    }
                    PaymentTypes.TNG_E_WALLET -> {
                        TngEWalletForm(
                            phoneNumber = phoneNumber,
                            onPhoneNumberChange = { phoneNumber = it }
                        )
                    }
                    PaymentTypes.CASH_ON_DELIVERY -> {
                        Text(
                            text = "No additional information needed for Cash on Delivery",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val details = when (selectedPaymentType) {
                                PaymentTypes.CREDIT_CARD -> mapOf(
                                    "cardNumber" to cardNumber,
                                    "cardHolderName" to cardHolderName,
                                    "expiryDate" to expiryDate,
                                    "cvv" to cvv,
                                    "lastFourDigits" to cardNumber.takeLast(4)
                                )
                                PaymentTypes.TNG_E_WALLET -> mapOf(
                                    "phoneNumber" to phoneNumber
                                )
                                PaymentTypes.CASH_ON_DELIVERY -> emptyMap()
                                else -> emptyMap()
                            }

                            val paymentMethod = PaymentMethod(
                                paymentType = selectedPaymentType,
                                details = details,
                                userId = "current_user_id" // Get from auth
                            )
                            onAddPaymentMethod(paymentMethod)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = when (selectedPaymentType) {
                            PaymentTypes.CREDIT_CARD ->
                                cardNumber.length == 16 &&
                                        cardHolderName.isNotBlank() &&
                                        expiryDate.length == 5 &&
                                        cvv.length == 3
                            PaymentTypes.TNG_E_WALLET ->
                                phoneNumber.length >= 10
                            PaymentTypes.CASH_ON_DELIVERY -> true
                            else -> true
                        }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
fun CreditCardForm(
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    cardHolderName: String,
    onCardHolderNameChange: (String) -> Unit,
    expiryDate: String,
    onExpiryDateChange: (String) -> Unit,
    cvv: String,
    onCvvChange: (String) -> Unit
) {
    // Error states
    var cardNumberError by remember { mutableStateOf<String?>(null) }
    var cardHolderError by remember { mutableStateOf<String?>(null) }
    var expiryError by remember { mutableStateOf<String?>(null) }
    var cvvError by remember { mutableStateOf<String?>(null) }

    // Validation functions
    fun validateCardNumber(number: String): String? {
        return when {
            number.isBlank() -> "Card number is required"
            number.length < 16 -> "Card number must be 16 digits"
            !isValidCardNumber(number) -> "Invalid card number"
            else -> null
        }
    }

    fun validateCardHolder(name: String): String? {
        return when {
            name.isBlank() -> "Cardholder name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            !name.matches(Regex("^[a-zA-Z\\s]+$")) -> "Name should only contain letters"
            else -> null
        }
    }

    fun validateExpiryDate(date: String): String? {
        return when {
            date.isBlank() -> "Expiry date is required"
            date.length < 5 -> "Expiry date format: MM/YY"
            !isValidExpiryDate(date) -> "Invalid or expired date"
            else -> null
        }
    }

    fun validateCVV(cvv: String): String? {
        return when {
            cvv.isBlank() -> "CVV is required"
            cvv.length < 3 -> "CVV must be 3 digits"
            !cvv.all { it.isDigit() } -> "CVV must be numeric"
            else -> null
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = cardHolderName,
            onValueChange = {
                if (it.all { char -> char.isLetter() || char.isWhitespace() }) {
                    onCardHolderNameChange(it)
                    cardHolderError = null
                }
            },
            label = { Text("Cardholder Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = cardHolderError != null,
            supportingText = {
                cardHolderError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        )

        OutlinedTextField(
            value = cardNumber,
            onValueChange = { input ->
                val digitsOnly = input.filter { it.isDigit() }
                if (digitsOnly.length <= 16) {
                    onCardNumberChange(digitsOnly)
                    cardNumberError = null
                }
            },
            label = { Text("Card Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = CreditCardTransformation(),
            isError = cardNumberError != null,
            supportingText = {
                cardNumberError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = expiryDate,
                onValueChange = { input ->
                    val digitsOnly = input.filter { it.isDigit() }
                    val formatted = when {
                        digitsOnly.length <= 2 -> digitsOnly
                        digitsOnly.length <= 4 -> "${digitsOnly.substring(0, 2)}/${digitsOnly.substring(2)}"
                        else -> "${digitsOnly.substring(0, 2)}/${digitsOnly.substring(2, 4)}"
                    }
                    if (formatted.length <= 5) {
                        onExpiryDateChange(formatted)
                        expiryError = null
                    }
                },
                label = { Text("MM/YY") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = expiryError != null,
                supportingText = {
                    expiryError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 10.sp
                        )
                    }
                }
            )

            OutlinedTextField(
                value = cvv,
                onValueChange = { input ->
                    if (input.length <= 3 && input.all { it.isDigit() }) {
                        onCvvChange(input)
                        cvvError = null
                    }
                },
                label = { Text("CVV") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = cvvError != null,
                supportingText = {
                    cvvError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 10.sp
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun TngEWalletForm(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit
) {
    var phoneError by remember { mutableStateOf<String?>(null) }

    fun validatePhoneNumber(phone: String): String? {
        return when {
            phone.isBlank() -> "Phone number is required"
            !phone.startsWith("01") -> "Malaysian phone numbers start with 01"
            phone.length < 10 -> "Phone number must be at least 10 digits"
            phone.length > 11 -> "Phone number must be at most 11 digits"
            !phone.all { it.isDigit() } -> "Phone number must contain only digits"
            else -> null
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Link your Touch 'n Go e-Wallet",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { input ->
                val digitsOnly = input.filter { it.isDigit() }
                if (digitsOnly.length <= 11) {
                    onPhoneNumberChange(digitsOnly)
                    phoneError = null
                }
            },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            placeholder = { Text("e.g., 0123456789") },
            isError = phoneError != null,
            supportingText = {
                phoneError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        )

        Text(
            text = "We'll send a verification code to this number",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

fun isValidCardNumber(cardNumber: String): Boolean {
    // Luhn algorithm implementation
    var sum = 0
    var alternate = false

    for (i in cardNumber.length - 1 downTo 0) {
        var n = cardNumber[i].toString().toInt()
        if (alternate) {
            n *= 2
            if (n > 9) {
                n = (n % 10) + 1
            }
        }
        sum += n
        alternate = !alternate
    }
    return (sum % 10 == 0)
}

fun isValidExpiryDate(expiryDate: String): Boolean {
    if (expiryDate.length != 5 || !expiryDate.contains("/")) return false

    val parts = expiryDate.split("/")
    if (parts.size != 2) return false

    try {
        val month = parts[0].toInt()
        val year = parts[1].toInt()

        if (month < 1 || month > 12) return false

        // Check if card is expired
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100
        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1

        if (year < currentYear || (year == currentYear && month < currentMonth)) {
            return false
        }

        return true
    } catch (e: NumberFormatException) {
        return false
    }
}

class CreditCardTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != 15) out += " "
        }
        return TransformedText(AnnotatedString(out), OffsetMapping.Identity)
    }
}

@Composable
fun AddressCard(
    address: DeliveryAddress,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    showDelete: Boolean = true
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Address") },
            text = { Text("Are you sure you want to delete this address?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .then(
                if (isSelected) {
                    Modifier.border(
                        2.dp,
                        DarkBrown,
                        RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Cream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = DarkBrown
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = address.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = address.addressLine,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${address.city}, ${address.state} ${address.postalCode}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showDelete) {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete address",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = DarkBrown
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    showDelete: Boolean = true
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Payment Method") },
            text = { Text("Are you sure you want to delete this payment method?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .then(
                if (isSelected) {
                    Modifier.border(
                        2.dp,
                        DarkBrown,
                        RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Cream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                PaymentTypes.getIcon(paymentMethod.paymentType),
                contentDescription = null,
                tint = DarkBrown
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = PaymentTypes.getDisplayName(paymentMethod.paymentType),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                // Display appropriate details
                when (paymentMethod.paymentType) {
                    PaymentTypes.CREDIT_CARD -> {
                        paymentMethod.lastFourDigits?.let { lastFour ->
                            Text(
                                text = "**** **** **** $lastFour",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                    PaymentTypes.TNG_E_WALLET -> {
                        paymentMethod.phoneNumber?.let { phone ->
                            Text(
                                text = "Phone: $phone",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                    PaymentTypes.CASH_ON_DELIVERY -> {
                        Text(
                            text = "Pay when order arrives",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showDelete && paymentMethod.paymentType != PaymentTypes.CASH_ON_DELIVERY) {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete payment method",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = DarkBrown
                    )
                }
            }
        }
    }
}


@Composable
fun OrderSummaryCard(
    subtotal: Double,
    deliveryFee: Double,
    tax: Double,
    total: Double
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryRow("Subtotal", subtotal)
        SummaryRow("Delivery Fee", deliveryFee)
        SummaryRow("Tax", tax)

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "RM ${String.format("%.2f", total)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = "RM ${String.format("%.2f", amount)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CartItemCard(cartItem: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(id = cartItem.imageRes),
            contentDescription = cartItem.name,

            modifier = Modifier
                .padding(8.dp)
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = cartItem.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Qty: ${cartItem.quantity}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Text(
            text = "RM ${String.format("%.2f", cartItem.totalPrice)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically)
        )
    }
}
@Composable
fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

// Sample data for preview
fun getSampleCheckoutUiState(): CheckoutUiState {
    return CheckoutUiState(
        cartItems = listOf(
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
                quantity = 2,
                isSelected = true,
                unitPrice = 100.00
            )
        ),
        order = Order(
            estimatedDelivery = 15.00,
            subTotal = 755.00,
            tax = 45.30,
            total = 815.30
        ),
        paymentMethods = listOf(
            PaymentMethod(
                id = 1,
                userId = "1",
                paymentType = PaymentTypes.CREDIT_CARD,
                details = mapOf(
                    "cardNumber" to "4111111111111111",
                    "lastFourDigits" to "1111",
                    "cardHolderName" to "John Doe",
                    "expiryDate" to "12/25",
                    "cvv" to "123"
                ),
                isSelected = true
            ),
            PaymentMethod(
                id = 2,
                userId = "1",
                paymentType = PaymentTypes.TNG_E_WALLET,
                details = mapOf("phoneNumber" to "0123456789"),
                isSelected = false
            ),
            PaymentMethod(
                id = 3,
                userId = "1",
                paymentType = PaymentTypes.CASH_ON_DELIVERY,
                details = emptyMap(),
                isSelected = false
            )
        ),
        selectedPaymentMethod = PaymentMethod(
            id = 1,
            userId = "1",
            paymentType = PaymentTypes.CREDIT_CARD,
            details = mapOf(
                "cardNumber" to "4111111111111111",
                "lastFourDigits" to "1111",
                "cardHolderName" to "John Doe",
                "expiryDate" to "12/25",
                "cvv" to "123"
            ),
            isSelected = true
        ),
        deliveryAddresses = listOf(
            DeliveryAddress(
                id = 1,
                name = "Home",
                userId = "1",
                addressLine = "123 Jalan Setapak, Taman Setapak",
                city = "Kuala Lumpur",
                state = "Selangor",
                postalCode = "53000",
                isDefault = true
            ),
            DeliveryAddress(
                id = 2,  // Fixed duplicate ID
                name = "Work",
                userId = "1",
                addressLine = "123 Jalan Setapak, Taman KLCC",
                city = "Kuala Lumpur",
                state = "Selangor",
                postalCode = "53000",
                isDefault = false
            )
        ),
        defaultAddress = DeliveryAddress(
            id = 1,
            name = "Home",
            userId = "1",
            addressLine = "123 Jalan Setapak, Taman Setapak",
            city = "Kuala Lumpur",
            state = "Selangor",
            postalCode = "53000",
            isDefault = true
        ),
        isLoading = false,
        error = null,
        checkoutSuccess = "1"
    )
}

@Composable
fun CheckoutRoute(
    onCheckoutSuccess: (String) -> Unit,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }

    val cartRepository = remember(database) {
        CartRepositoryImpl(database.cartItemDao(), CartItemMapper())
    }
    val orderRepository = remember(database) {
        OrderRepository(database.orderDao())
    }

    val paymentRepository = remember(database) {
        PaymentRepository(database.paymentMethodDao(), PaymentMethodMapper())
    }

    val addressRepository = remember(database) {
        AddressRepository(database.deliveryAddressDao(), DeliveryAddressMapper())
    }

    val checkoutRepository = remember(
        database,
        cartRepository,
        orderRepository,
        paymentRepository,
        addressRepository
    ) {
        CheckoutRepository(
            cartRepository = cartRepository,
            orderRepository = orderRepository
        )
    }

    val viewModel: CheckoutViewModel = viewModel(
        factory = CheckoutViewModelFactory(cartRepository, checkoutRepository, authViewModel, addressRepository, paymentRepository)
    )

    val uiState by viewModel.uiState.collectAsState()

    CheckoutScreen(
        uiState = uiState,
        onCheckoutSuccess = onCheckoutSuccess,
        onSelectPaymentMethod = viewModel::selectPaymentMethod,
        onSetDefaultAddress = viewModel::setDefaultAddress,
        onProcessCheckout = viewModel::processCheckoutUi,
        onAddDeliveryAddress = viewModel::addDeliveryAddress,
        onAddPaymentMethod = viewModel::addPaymentMethod,
        onDeleteDeliveryAddress = viewModel::removeDeliveryAddress,
        onDeletePaymentMethod = viewModel::removePaymentMethod,
        onClearError = viewModel::clearError,
        onClearCheckoutSuccess = viewModel::clearCheckoutSuccess,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun JoMaloneCheckoutScreenPreview() {
    JoMaloneMobileApplicationTheme {
        CheckoutScreen(
            onCheckoutSuccess = {_ -> },
            onSelectPaymentMethod = {},
            onSetDefaultAddress = {},
            onProcessCheckout = { _, _ -> },
            onAddDeliveryAddress = {},
            onAddPaymentMethod = {},
            onDeleteDeliveryAddress = {},
            onDeletePaymentMethod = {},
            onClearError = {},
            onClearCheckoutSuccess = {},
            uiState = getSampleCheckoutUiState(),


            )
    }
}
