package com.tecfort.navigationtest.ui.account

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class OtpScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rendersTheEmailCodeWasSentTo() {
        composeTestRule.setContent {
            OtpScreen(
                email = "user@example.com",
                uiState = AccountSecurityUiState(),
                onCodeChange = {},
                onVerifyClick = {},
                onResendClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Enter the 6-digit code sent to user@example.com").assertExists()
    }

    @Test
    fun typingInCodeField_invokesOnCodeChange() {
        var code: String? = null
        composeTestRule.setContent {
            OtpScreen(
                email = "user@example.com",
                uiState = AccountSecurityUiState(),
                onCodeChange = { code = it },
                onVerifyClick = {},
                onResendClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Code").performTextInput("123456")

        assert(code == "123456") { "Expected onCodeChange(\"123456\") but was $code" }
    }

    @Test
    fun clickingVerify_whenEnabled_invokesOnVerifyClick() {
        var verified = false
        composeTestRule.setContent {
            OtpScreen(
                email = "user@example.com",
                uiState = AccountSecurityUiState(code = "123456"),
                onCodeChange = {},
                onVerifyClick = { verified = true },
                onResendClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Verify").assertHasClickAction()
        composeTestRule.onNodeWithText("Verify").performClick()

        assert(verified) { "Expected onVerifyClick to be invoked" }
    }

    @Test
    fun resendLink_disabledDuringCooldown_showsSecondsRemaining() {
        composeTestRule.setContent {
            OtpScreen(
                email = "user@example.com",
                uiState = AccountSecurityUiState(resendCooldownSeconds = 30),
                onCodeChange = {},
                onVerifyClick = {},
                onResendClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Resend code in 30s").assertIsNotEnabled()
    }

    @Test
    fun resendLink_enabledOnceCooldownEnds() {
        var resent = false
        composeTestRule.setContent {
            OtpScreen(
                email = "user@example.com",
                uiState = AccountSecurityUiState(resendCooldownSeconds = 0),
                onCodeChange = {},
                onVerifyClick = {},
                onResendClick = { resent = true },
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Resend code").assertHasClickAction()
        composeTestRule.onNodeWithText("Resend code").performClick()

        assert(resent) { "Expected onResendClick to be invoked" }
    }

    @Test
    fun errorMessage_isDisplayed() {
        composeTestRule.setContent {
            OtpScreen(
                email = "user@example.com",
                uiState = AccountSecurityUiState(errorMessage = "Incorrect code. Please try again."),
                onCodeChange = {},
                onVerifyClick = {},
                onResendClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Incorrect code. Please try again.").assertExists()
    }
}
