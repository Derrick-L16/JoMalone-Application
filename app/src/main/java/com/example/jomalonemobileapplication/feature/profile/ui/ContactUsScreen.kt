package com.example.jomalonemobileapplication.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jomalonemobileapplication.feature.login.ui.AuthViewModel
import com.example.jomalonemobileapplication.theme.Background
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val authViewModel: AuthViewModel = viewModel()
    var selectedCategory by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentUserId by remember { mutableStateOf<String?>(null) }

    // Categories for the dropdown
    val categories = listOf(
        "General Inquiry",
        "Product Information",
        "Order Issues",
        "Payment Problems",
        "Shipping & Delivery",
        "Returns & Refunds",
        "Technical Support",
        "Feedback & Suggestions"
    )

    // Get current user's sequential ID when screen loads
    LaunchedEffect(Unit) {
        authViewModel.getCurrentUserSequentialId { userId ->
            currentUserId = userId
        }
    }

    // Function to submit contact form to Firebase
    suspend fun submitContactForm(
        userId : String,
        category: String,
        message: String
    ): Result<String> {
        return try {
            val db = FirebaseFirestore.getInstance()

            // get the next feedback counter
            val feedbackCounter = getNextFeedbackCounter(userId)

            val feedbackKey = "feedback_$feedbackCounter"
            val feedbackEntry = hashMapOf(
                "category" to category,
                "message" to message,
            )

            val feedbackUpdate = hashMapOf<String,Any>(
                "feedback.$feedbackKey" to feedbackEntry
            )

            db.collection("users")
                .document(userId)
                .update(feedbackUpdate)
                .await()

            Result.success("Message submitted successfully")
        } catch (e: Exception) {
            // If feedbacks field doesn't exist, create it
            try {
                val db = FirebaseFirestore.getInstance()
                val feedbackCounter = getNextFeedbackCounter(userId)

                val feedbackKey = "feedback_$feedbackCounter"
                val feedbackEntry = hashMapOf(
                    "category" to category,
                    "message" to message,
                )

                val feedbacksMap = hashMapOf(
                    feedbackKey to feedbackEntry
                )

                db.collection("users")
                    .document(userId)
                    .update("feedbacks", feedbacksMap)
                    .await()

                Result.success("Feedback submitted successfully")
            } catch (createException: Exception) {
                Result.failure(createException)
            }
        }
    }

    // Handle success message
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            kotlinx.coroutines.delay(3000)
            successMessage = null
            onBack()
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contact Us",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "We're here to help! Please select a category and describe your issue.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Category Dropdown
            Text(
                text = "Category *",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text(
                        text = selectedCategory.ifEmpty { "Select a category" },
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                                errorMessage = null
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description TextField
            Text(
                text = "Description *",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    errorMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Please describe your issue or question in detail...") },
                maxLines = 5,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    // Validate inputs
                    when {
                        selectedCategory.isEmpty() -> {
                            errorMessage = "Please select a category"
                        }

                        description.trim().isEmpty() -> {
                            errorMessage = "Please provide a description"
                        }

                        description.trim().length < 10 -> {
                            errorMessage =
                                "Please provide a more detailed description (at least 10 characters)"
                        }

                        currentUserId == null -> {
                            errorMessage = "User information not loaded yet. Please wait and try again."
                        }

                        else -> {
                            // Submit form to Firebase
                            val userEmail = authViewModel.getCurrentUserEmail()

                            if (userEmail != null) {
                                isLoading = true
                                errorMessage = null

                                kotlinx.coroutines.MainScope().launch {
                                    val result = submitContactForm(
                                        userId = currentUserId!!,
                                        category = selectedCategory,
                                        message = description.trim()
                                    )

                                    isLoading = false

                                    result.onSuccess {
                                        successMessage =
                                            "Your message has been sent successfully! We will get back to you within 24 hours."
                                        selectedCategory = ""
                                        description = ""
                                    }.onFailure { error ->
                                        errorMessage = "Failed to send message: ${error.message}"
                                    }
                                }
                            } else {
                                errorMessage = "Please log in to send a message"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && currentUserId != null // Disable button until user ID is loaded
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sending...")
                } else {
                    Text("Send Message")
                }
            }

            // Show loading indicator for user ID
            if (currentUserId == null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Loading user information...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Error Message
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Success Message
            successMessage?.let { success ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = success,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Help Text
            Text(
                text = "For urgent matters, please call our customer service at +60 1234-567890 during business hours (9 AM - 6 PM, Monday to Friday).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// function to get the next sequential feedback counter for a user
private suspend fun getNextFeedbackCounter(userId: String): Int {
    return try {
        val db = FirebaseFirestore.getInstance()
        val counterDoc = db.collection("userFeedbackCounters").document(userId)

        val result = db.runTransaction { transaction ->
            val snapshot = transaction.get(counterDoc)
            val currentCount = if (snapshot.exists()) {
                snapshot.getLong("count")?.toInt() ?: 0
            } else {
                0
            }
            val newCount = currentCount + 1
            transaction.set(counterDoc, mapOf("count" to newCount))
            newCount
        }.await()

        result
    } catch (e: Exception) {
        // Fallback: use timestamp if counter fails
        System.currentTimeMillis().toInt()
    }
}


@Preview(showBackground = true)
@Composable
fun ContactUsScreenPreview() {
    ContactUsScreen()
}