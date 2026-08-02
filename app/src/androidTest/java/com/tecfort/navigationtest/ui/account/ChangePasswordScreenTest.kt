package com.tecfort.navigationtest.ui.account

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class ChangePasswordScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun normalEntry_showsCurrentPasswordField() {
        composeTestRule.setContent {
            ChangePasswordScreen(
                isReset = false,
                uiState = AccountSecurityUiState(),
                onCurrentPasswordChange = {},
                onNewPasswordChange = {},
                onConfirmPasswordChange = {},
                onSubmitClick = {},
                onForgotPasswordClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Current password").assertExists()
        composeTestRule.onNodeWithText("Forgot password?").assertExists()
    }

    @Test
    fun resetEntry_hidesCurrentPasswordFieldAndForgotLink() {
        composeTestRule.setContent {
            ChangePasswordScreen(
                isReset = true,
                uiState = AccountSecurityUiState(),
                onCurrentPasswordChange = {},
                onNewPasswordChange = {},
                onConfirmPasswordChange = {},
                onSubmitClick = {},
                onForgotPasswordClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Current password").assertDoesNotExist()
        composeTestRule.onNodeWithText("Forgot password?").assertDoesNotExist()
    }

    @Test
    fun typingInNewPasswordField_invokesOnNewPasswordChange() {
        var newPassword: String? = null
        composeTestRule.setContent {
            ChangePasswordScreen(
                isReset = true,
                uiState = AccountSecurityUiState(),
                onCurrentPasswordChange = {},
                onNewPasswordChange = { newPassword = it },
                onConfirmPasswordChange = {},
                onSubmitClick = {},
                onForgotPasswordClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("New password").performTextInput("secretpw1")

        assert(newPassword == "secretpw1") { "Expected onNewPasswordChange(\"secretpw1\") but was $newPassword" }
    }

    @Test
    fun clickingSave_whenEnabled_invokesOnSubmitClick() {
        var submitted = false
        composeTestRule.setContent {
            ChangePasswordScreen(
                isReset = true,
                uiState = AccountSecurityUiState(newPassword = "password1", confirmPassword = "password1"),
                onCurrentPasswordChange = {},
                onNewPasswordChange = {},
                onConfirmPasswordChange = {},
                onSubmitClick = { submitted = true },
                onForgotPasswordClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Save new password").assertHasClickAction()
        composeTestRule.onNodeWithText("Save new password").performClick()

        assert(submitted) { "Expected onSubmitClick to be invoked" }
    }

    @Test
    fun errorMessage_isDisplayed() {
        composeTestRule.setContent {
            ChangePasswordScreen(
                isReset = false,
                uiState = AccountSecurityUiState(errorMessage = "Current password is incorrect."),
                onCurrentPasswordChange = {},
                onNewPasswordChange = {},
                onConfirmPasswordChange = {},
                onSubmitClick = {},
                onForgotPasswordClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Current password is incorrect.").assertExists()
    }

    @Test
    fun clickingShowPasswords_revealsHideOption() {
        composeTestRule.setContent {
            ChangePasswordScreen(
                isReset = true,
                uiState = AccountSecurityUiState(),
                onCurrentPasswordChange = {},
                onNewPasswordChange = {},
                onConfirmPasswordChange = {},
                onSubmitClick = {},
                onForgotPasswordClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Show passwords").performClick()

        composeTestRule.onNodeWithText("Hide passwords").assertExists()
    }
}
