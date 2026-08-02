package com.tecfort.navigationtest.ui.account

/** Digits in a one-time code, e.g. "123456". */
internal const val OTP_LENGTH = 6

/** Minimum acceptable new-password length, mirrored client-side from [com.tecfort.navigationtest.data.repository.DefaultAuthRepository]. */
internal const val MIN_PASSWORD_LENGTH = 8

/**
 * State shared by [ChangePasswordScreen], [ForgotPasswordScreen] and [OtpScreen] for one
 * account-security flow - see [com.tecfort.navigationtest.navigation.AccountDest].
 */
data class AccountSecurityUiState(
    val email: String = "",
    val code: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    /** Seconds left before "Resend code" is re-enabled; 0 means it is enabled now. */
    val resendCooldownSeconds: Int = 0,
) {
    val canSendCode: Boolean get() = email.contains("@") && !isSubmitting

    val canVerifyCode: Boolean get() = code.length == OTP_LENGTH && !isSubmitting

    /** @param isReset when true, [currentPassword] is not required. */
    fun canSubmitPassword(isReset: Boolean): Boolean =
        !isSubmitting &&
            (isReset || currentPassword.isNotEmpty()) &&
            newPassword.length >= MIN_PASSWORD_LENGTH &&
            newPassword == confirmPassword
}
