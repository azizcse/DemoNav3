package com.tecfort.navigationtest.ui.account

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class ForgotPasswordScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun typingInEmailField_invokesOnEmailChange() {
        var email: String? = null
        composeTestRule.setContent {
            ForgotPasswordScreen(
                uiState = AccountSecurityUiState(),
                onEmailChange = { email = it },
                onSendCodeClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Email").performTextInput("user@example.com")

        assert(email == "user@example.com") { "Expected onEmailChange(\"user@example.com\") but was $email" }
    }

    @Test
    fun sendCodeButton_disabledUntilEmailLooksValid() {
        composeTestRule.setContent {
            ForgotPasswordScreen(
                uiState = AccountSecurityUiState(email = "not-an-email"),
                onEmailChange = {},
                onSendCodeClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Send code").assertIsNotEnabled()
    }

    @Test
    fun clickingSendCode_whenEnabled_invokesOnSendCodeClick() {
        var clicked = false
        composeTestRule.setContent {
            ForgotPasswordScreen(
                uiState = AccountSecurityUiState(email = "user@example.com"),
                onEmailChange = {},
                onSendCodeClick = { clicked = true },
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Send code").assertHasClickAction()
        composeTestRule.onNodeWithText("Send code").performClick()

        assert(clicked) { "Expected onSendCodeClick to be invoked" }
    }

    @Test
    fun errorMessage_isDisplayed() {
        composeTestRule.setContent {
            ForgotPasswordScreen(
                uiState = AccountSecurityUiState(errorMessage = "Enter a valid email address."),
                onEmailChange = {},
                onSendCodeClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Enter a valid email address.").assertExists()
    }
}
