package com.tecfort.navigationtest.ui.account

import com.tecfort.navigationtest.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AccountSecurityViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verifyOtp with the wrong code sets errorMessage and does not invoke onVerified`() = runTest {
        val viewModel = AccountSecurityViewModel(FakeAuthRepository())
        viewModel.onCodeChange("000000")

        var verified = false
        viewModel.verifyOtp(email = "user@example.com", onVerified = { verified = true })
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(verified)
        assertEquals("wrong code", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `verifyOtp with the right code invokes onVerified`() = runTest {
        val viewModel = AccountSecurityViewModel(FakeAuthRepository())
        viewModel.onCodeChange(VALID_CODE)

        var verified = false
        viewModel.verifyOtp(email = "user@example.com", onVerified = { verified = true })
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(verified)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onCodeChange strips non-digits and caps at OTP_LENGTH`() {
        val viewModel = AccountSecurityViewModel(FakeAuthRepository())

        viewModel.onCodeChange("1a2b3c4d5e6f7g8h")

        assertEquals("123456", viewModel.uiState.value.code)
    }

    @Test
    fun `submitPassword does not call the repository when new and confirm differ`() = runTest {
        val repository = FakeAuthRepository()
        val viewModel = AccountSecurityViewModel(repository)
        viewModel.onCurrentPasswordChange("oldpassword")
        viewModel.onNewPasswordChange("newpassword1")
        viewModel.onConfirmPasswordChange("newpassword2")

        var succeeded = false
        viewModel.submitPassword(isReset = false, onSuccess = { succeeded = true })
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(succeeded)
        assertEquals(0, repository.changePasswordCallCount)
    }

    @Test
    fun `submitPassword with isReset sends a null currentPassword`() = runTest {
        val repository = FakeAuthRepository()
        val viewModel = AccountSecurityViewModel(repository)
        viewModel.onNewPasswordChange("newpassword1")
        viewModel.onConfirmPasswordChange("newpassword1")

        var succeeded = false
        viewModel.submitPassword(isReset = true, onSuccess = { succeeded = true })
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(succeeded)
        assertNull(repository.lastCurrentPassword)
    }

    @Test
    fun `sendOtp starts a resend cooldown that counts down`() = runTest {
        val viewModel = AccountSecurityViewModel(FakeAuthRepository())
        viewModel.onEmailChange("user@example.com")

        viewModel.sendOtp(onSent = {})
        // runCurrent(), not advanceUntilIdle(): the cooldown coroutine ticks every simulated
        // second for 30 seconds, so draining all scheduled work would run it to completion (0)
        // instead of leaving it mid-countdown.
        testDispatcher.scheduler.runCurrent()
        assertEquals(30, viewModel.uiState.value.resendCooldownSeconds)

        testDispatcher.scheduler.advanceTimeBy(3_000)
        testDispatcher.scheduler.runCurrent()
        assertEquals(27, viewModel.uiState.value.resendCooldownSeconds)
    }

    private companion object {
        const val VALID_CODE = "123456"
    }

    private class FakeAuthRepository : AuthRepository {
        var changePasswordCallCount = 0
            private set
        var lastCurrentPassword: String? = null
            private set

        override suspend fun sendOtp(email: String): Result<Unit> = Result.success(Unit)

        override suspend fun verifyOtp(email: String, code: String): Result<Unit> =
            if (code == VALID_CODE) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("wrong code"))
            }

        override suspend fun changePassword(currentPassword: String?, newPassword: String): Result<Unit> {
            changePasswordCallCount++
            lastCurrentPassword = currentPassword
            return Result.success(Unit)
        }
    }
}
