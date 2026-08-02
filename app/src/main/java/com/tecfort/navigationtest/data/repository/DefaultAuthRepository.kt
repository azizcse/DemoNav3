package com.tecfort.navigationtest.data.repository

import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * In-memory [AuthRepository] implementation. Swap this out for a network-backed implementation
 * without touching any callers of [AuthRepository]. Accepts [VALID_OTP] as the only correct code
 * and [CURRENT_PASSWORD] as the only correct current password, simulating a fixed test account.
 */
class DefaultAuthRepository @Inject constructor() : AuthRepository {

    override suspend fun sendOtp(email: String): Result<Unit> {
        delay(NETWORK_DELAY_MS)
        return Result.success(Unit)
    }

    override suspend fun verifyOtp(email: String, code: String): Result<Unit> {
        delay(NETWORK_DELAY_MS)
        return if (code == VALID_OTP) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Incorrect code. Please try again."))
        }
    }

    override suspend fun changePassword(currentPassword: String?, newPassword: String): Result<Unit> {
        delay(NETWORK_DELAY_MS)
        if (currentPassword != null && currentPassword != CURRENT_PASSWORD) {
            return Result.failure(IllegalArgumentException("Current password is incorrect."))
        }
        if (newPassword.length < MIN_PASSWORD_LENGTH) {
            return Result.failure(IllegalArgumentException("Password must be at least $MIN_PASSWORD_LENGTH characters."))
        }
        return Result.success(Unit)
    }

    private companion object {
        const val NETWORK_DELAY_MS = 800L
        const val VALID_OTP = "123456"
        const val CURRENT_PASSWORD = "oldpassword"
        const val MIN_PASSWORD_LENGTH = 8
    }
}
