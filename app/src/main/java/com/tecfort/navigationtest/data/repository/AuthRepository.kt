package com.tecfort.navigationtest.data.repository

/**
 * Account-security operations backing the change-password / forgot-password / OTP flow. Each
 * method returns a [Result] whose failure's [Throwable.message] is user-facing - it is surfaced
 * verbatim as the on-screen error.
 */
interface AuthRepository {

    /** Sends a one-time code to [email]. */
    suspend fun sendOtp(email: String): Result<Unit>

    /** Verifies [code] was the one most recently sent to [email]. */
    suspend fun verifyOtp(email: String, code: String): Result<Unit>

    /**
     * @param currentPassword the account's existing password, or null when identity was already
     *   proven by a verified OTP - see [com.tecfort.navigationtest.navigation.AccountDest.ChangePassword.isReset].
     */
    suspend fun changePassword(currentPassword: String?, newPassword: String): Result<Unit>
}
