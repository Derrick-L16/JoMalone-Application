package com.example.jomalonemobileapplication.feature.profile.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun DeleteAccountDialog(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    showSuccess: Boolean = false,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    onPasswordChange: () -> Unit = {},
    onSuccessConfirmed: () -> Unit = {}
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    when {
        showSuccess -> {
            // success dialog, user click OK to navigate back to sign in
            AlertDialog(
                onDismissRequest = { }, // not allow to press outside
                title = { Text("Account Deleted Successfully") },
                text = { Text("Your account has been permanently deleted.") },
                confirmButton = {
                    Button(onClick = onSuccessConfirmed) {
                        Text("OK")
                    }
                }
            )
        }

        else -> {
            // password confirmation dialog
            AlertDialog(
                onDismissRequest = {
                    if (!isLoading) onDismiss()
                },
                title = { Text("Delete Account") },
                text = {
                    Column {
                        Text("Enter your password to confirm account deletion:")

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                onPasswordChange()
                            },
                            label = { Text("Password") },
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible)
                                            Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide" else "Show"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            isError = errorMessage != null,
                            placeholder = { Text("Enter your password") },
                            singleLine = true
                        )

                        errorMessage?.let { error ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onConfirm(password)
                        },
                        enabled = !isLoading && password.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Delete Account")
                        }
                    }
                },
                dismissButton = {
                    Button(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
