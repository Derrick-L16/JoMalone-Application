package com.example.jomalonemobileapplication.feature.profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.feature.login.ui.AuthViewModel
import com.example.jomalonemobileapplication.theme.Cream


@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    onNavigationToProfileInformation: () -> Unit = {},
    onNavigateToFavouritePerfume: () -> Unit = {},
    onNavigateToPaymentHistory: () -> Unit = {},
    onNavigateToContactUs: () -> Unit = {},
    onNavigateToScentPreference: () -> Unit = {},
    onNavigateToCustomizationHistory: () -> Unit,
    onNavigateToLogout: () -> Unit = {},
    onAccountDeleted: () -> Unit = {},
) {
    val viewModel: AuthViewModel = viewModel()
    val profileState by viewModel.userProfileState.collectAsState()
    val deleteState by viewModel.deleteAccountState.collectAsState()
    val showLogoutDialog by viewModel.showLogoutDialog.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    // State for delete confirmation dialog
    if (deleteState.showDialog) {
        DeleteAccountDialog(
            isLoading = deleteState.isLoading,
            errorMessage = deleteState.errorMessage,
            showSuccess = deleteState.showSuccess,
            onConfirm = { password ->
                viewModel.deleteAccountWithPassword(password)
            },
            onDismiss = {
                viewModel.cancelAccountDeletion()
            },
            onPasswordChange = {
                viewModel.clearDeleteAccountError()
            },
            onSuccessConfirmed = {
                onAccountDeleted()
                viewModel.cancelAccountDeletion() // reset the state
            }
        )
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                viewModel.performLogout()
                onNavigateToLogout()
            },
            onDismiss = {
                viewModel.dismissLogoutConfirmation()
            }
        )
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxWidth()
            .fillMaxSize()
    ) {
        // User Profile Card
        UserProfileCard(
            name = profileState.name,
            phoneNumber = profileState.phoneNumber,
            email = profileState.email,
            isLoading = profileState.isLoading,
            onClick = onNavigationToProfileInformation
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Menu Items
        FavouritePerfumeButton(onClick = onNavigateToFavouritePerfume)

        Spacer(modifier = Modifier.height(10.dp))

        PaymentHistoryButton(onClick = onNavigateToPaymentHistory)

        Spacer(modifier = Modifier.height(10.dp))

        ContactUsButton(onClick = onNavigateToContactUs)

        Spacer(modifier = Modifier.height(10.dp))

        ScentPreferenceButton(onClick = onNavigateToScentPreference)

        Spacer(modifier = Modifier.height(10.dp))

        CustomizationHistoryButton(onClick = onNavigateToCustomizationHistory)

        Spacer(modifier = Modifier.height(10.dp))

        DeleteAccountButton(
            onClick = { viewModel.startAccountDeletion() },
            isLoading = deleteState.isLoading
        )

        Spacer(modifier = Modifier.height(10.dp))

        LogoutButton(onClick = {
            viewModel.showLogoutConfirmation()
        })
    }
}

@Composable
fun UserProfileCard(
    name: String,
    phoneNumber: String,
    email: String,
    isLoading: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Cream)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left section: Profile Icon + Info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile picture (using a placeholder)
                    Image(
                        painter = painterResource(id = R.drawable.profile_photo),
                        contentDescription = "Profile Icon",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        // User Name
                        Text(
                            text = name.ifEmpty { "User" },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )

                        // Phone Number
                        if (phoneNumber.isNotEmpty()) {
                            Text(
                                text = phoneNumber,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        // Email
                        Text(
                            text = email.ifEmpty { "No email" },
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                // Right section: Arrow button
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Edit Info",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun FavouritePerfumeButton(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Cream, shape = MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.FavoriteBorder,
            contentDescription = "Favourite Perfume",
            tint = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Favourite Perfume",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PaymentHistoryButton(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Cream, shape = MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Payment,
            contentDescription = "Payment History",
            tint = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Payment History",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ContactUsButton(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Cream, shape = MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.SupportAgent,
            contentDescription = "Contact Us",
            tint = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Contact Us",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ScentPreferenceButton(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Cream, shape = MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Science,
            contentDescription = "Scent Preference",
            tint = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "My Scent Preference",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CustomizationHistoryButton(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Cream, shape = MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Build,
            contentDescription = "Customization History",
            tint = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Customization History",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DeleteAccountButton(
    onClick: () -> Unit = {},
    isLoading: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Cream, shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !isLoading) { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = "Delete Account",
            tint = Color.Black,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Delete Account",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Cream, shape = MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.Logout,
            contentDescription = "Logout",
            tint = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Logout",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileContentPreview() {
    ProfileContent(
        onNavigateToCustomizationHistory = {}
    )
}