package com.example.jomalonemobileapplication.feature.profile.ui

import android.R.attr.enabled
import android.R.attr.label
import android.R.attr.name
import android.R.attr.phoneNumber
import android.R.attr.singleLine
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jomalonemobileapplication.theme.Background
import com.example.jomalonemobileapplication.feature.login.ui.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInformationScreen(
    modifier: Modifier = Modifier.background(Background),
    onBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
) {
    val viewModel: AuthViewModel = viewModel()
    val profileState by viewModel.userProfileState.collectAsState()

    // initialize with current profile data
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }


    // Update local state when profileState changes
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    Scaffold(
        modifier = modifier,
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
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
        ) {
            // Display current email (read-only)
            OutlinedTextField(
                value = profileState.email,
                onValueChange = { },
                label = { Text("Email")},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                readOnly = true
            )

            Text(
                text = "Email cannot be changed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null // Clear error when user types
                },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                enabled = !profileState.isLoading,
                placeholder = { Text("Enter your name") },
                singleLine = true
            )

            // avoid save empty name
            nameError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+60",
                    modifier = Modifier.padding(end = 8.dp),
                    color = Color.Gray
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { newValue ->
                        if (newValue.matches("\\d*".toRegex()) && newValue.length <= 10) {
                            phoneNumber = newValue
                        }
                    },
                    label = { Text("Phone Number") },
                    placeholder = { Text("1137982045") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = phoneError != null,
                    enabled = !profileState.isLoading,
                    singleLine = true
                )
            }
            Text(
                text = "Malaysian phone number (8-9 digits)",
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 4.dp),
                style = MaterialTheme.typography.bodySmall
            )

            // avoid save empty phone number
            phoneError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    nameError = null
                    phoneError = null

                    // Validate inputs - check if at least one field is provided
                    val trimmedName = name.trim()
                    val trimmedPhone = phoneNumber.trim()

                    if (trimmedName.isBlank() && trimmedPhone.isBlank()) {
                        nameError = "Please enter at least name or phone number"
                        phoneError = "Please enter at least name or phone number"
                        return@Button
                    }

                    // Check if values are different from current profile
                    val isNameChanged = trimmedName != profileState.name
                    val isPhoneChanged = trimmedPhone != profileState.phoneNumber

                    if (!isNameChanged && !isPhoneChanged) {
                        nameError = "No changes detected"
                        return@Button
                    }

                    viewModel.updateUserProfile(trimmedName, trimmedPhone) {
                        onSaveSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !profileState.isLoading
            ) {
                if (profileState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Save Changes")
                }
            }

            // show error message from viewModel
            profileState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Show help text
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You can update either your name or phone number, or both. Leave fields empty if you don't want to change them.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileInformationPreview() {
    ProfileInformationScreen()
}
