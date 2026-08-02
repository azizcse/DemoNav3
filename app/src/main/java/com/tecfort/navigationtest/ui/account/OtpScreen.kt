package com.tecfort.navigationtest.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecfort.navigationtest.ui.components.AppTopBar

/**
 * Lets the user enter the one-time code sent to [email], then calls [onVerified] on success.
 * Part of the account-security flow - see [com.tecfort.navigationtest.navigation.AccountDest].
 */
@Composable
fun OtpScreen(
    email: String,
    onVerified: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccountSecurityViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    OtpScreen(
        email = email,
        uiState = uiState,
        onCodeChange = viewModel::onCodeChange,
        onVerifyClick = { viewModel.verifyOtp(email = email, onVerified = onVerified) },
        onResendClick = { viewModel.sendOtp(onSent = {}) },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun OtpScreen(
    email: String,
    uiState: AccountSecurityUiState,
    onCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Verify Code", onBackClick = onBackClick) },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Enter the $OTP_LENGTH-digit code sent to $email")
            OutlinedTextField(
                value = uiState.code,
                onValueChange = onCodeChange,
                label = { Text("Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            uiState.errorMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }

            Button(onClick = onVerifyClick, enabled = uiState.canVerifyCode) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Verify")
                }
            }

            TextButton(
                onClick = onResendClick,
                enabled = uiState.resendCooldownSeconds == 0 && !uiState.isSubmitting,
            ) {
                Text(
                    if (uiState.resendCooldownSeconds > 0) {
                        "Resend code in ${uiState.resendCooldownSeconds}s"
                    } else {
                        "Resend code"
                    },
                )
            }
        }
    }
}
