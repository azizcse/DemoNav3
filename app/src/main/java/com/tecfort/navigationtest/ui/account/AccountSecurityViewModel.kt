package com.tecfort.navigationtest.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecfort.navigationtest.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel shared by [ChangePasswordScreen], [ForgotPasswordScreen] and [OtpScreen]. One
 * instance exists per account-security flow scope (see
 * [com.tecfort.navigationtest.navigation.AccountDest]), so the email typed on ForgotPassword is
 * still visible once the flow reaches ChangePassword.
 */
@HiltViewModel
class AccountSecurityViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountSecurityUiState())
    val uiState: StateFlow<AccountSecurityUiState> = _uiState.asStateFlow()

    private var resendCooldownJob: Job? = null

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }

    fun onCodeChange(value: String) {
        val digitsOnly = value.filter(Char::isDigit).take(OTP_LENGTH)
        _uiState.update { it.copy(code = digitsOnly, errorMessage = null) }
    }

    fun onCurrentPasswordChange(value: String) =
        _uiState.update { it.copy(currentPassword = value, errorMessage = null) }

    fun onNewPasswordChange(value: String) =
        _uiState.update { it.copy(newPassword = value, errorMessage = null) }

    fun onConfirmPasswordChange(value: String) =
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }

    fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }

    /** Sends a code to the entered email, then hands it to [onSent] so the caller can navigate. */
    fun sendOtp(onSent: (email: String) -> Unit) {
        val state = _uiState.value
        if (!state.canSendCode) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = repository.sendOtp(state.email)
            _uiState.update { it.copy(isSubmitting = false) }
            result.fold(
                onSuccess = {
                    startResendCooldown()
                    onSent(state.email)
                },
                onFailure = { error -> _uiState.update { it.copy(errorMessage = error.message) } },
            )
        }
    }

    /** Verifies the entered code against [email], then calls [onVerified] on success. */
    fun verifyOtp(email: String, onVerified: () -> Unit) {
        val state = _uiState.value
        if (!state.canVerifyCode) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = repository.verifyOtp(email, state.code)
            _uiState.update { it.copy(isSubmitting = false) }
            result.fold(
                onSuccess = { onVerified() },
                onFailure = { error -> _uiState.update { it.copy(errorMessage = error.message) } },
            )
        }
    }

    /**
     * @param isReset true to skip the current-password check - see
     *   [com.tecfort.navigationtest.navigation.AccountDest.ChangePassword.isReset].
     */
    fun submitPassword(isReset: Boolean, onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.canSubmitPassword(isReset)) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = repository.changePassword(
                currentPassword = if (isReset) null else state.currentPassword,
                newPassword = state.newPassword,
            )
            _uiState.update { it.copy(isSubmitting = false) }
            result.fold(
                onSuccess = { onSuccess() },
                onFailure = { error -> _uiState.update { it.copy(errorMessage = error.message) } },
            )
        }
    }

    /** Ticks [AccountSecurityUiState.resendCooldownSeconds] down to zero, one second at a time. */
    private fun startResendCooldown() {
        resendCooldownJob?.cancel()
        _uiState.update { it.copy(resendCooldownSeconds = RESEND_COOLDOWN_SECONDS) }
        resendCooldownJob = viewModelScope.launch {
            while (_uiState.value.resendCooldownSeconds > 0) {
                delay(COOLDOWN_TICK_MS)
                _uiState.update { it.copy(resendCooldownSeconds = it.resendCooldownSeconds - 1) }
            }
        }
    }

    private companion object {
        const val RESEND_COOLDOWN_SECONDS = 30
        const val COOLDOWN_TICK_MS = 1_000L
    }
}
