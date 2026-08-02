package com.tecfort.navigationtest.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Verifies that [AccountDest.ChangePassword], [AccountDest.ForgotPassword] and [AccountDest.Otp]
 * all share one [FlowScopedViewModelStoreNavEntryDecorator] scope, mirroring
 * [FlowScopedViewModelStoreNavEntryDecoratorTest] for the item flow. Uses a plain [ViewModel]
 * (not `hiltViewModel()`) for the same reason that test does: the compose rule's host activity is
 * not `@AndroidEntryPoint`.
 */
class AccountFlowScopeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    class CounterViewModel : ViewModel() {
        var cleared = false
            private set

        override fun onCleared() {
            cleared = true
        }
    }

    private fun setContentWithBackStack(backStack: NavBackStack<NavKey>, seen: MutableList<CounterViewModel>) {
        composeTestRule.setContent {
            NavDisplay(
                backStack = backStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                    rememberFlowScopedViewModelStoreNavEntryDecorator(backStack),
                ),
                entryProvider = entryProvider {
                    entry<Dest.Setting> { Text("setting") }
                    flowScopedEntry<AccountDest.ForgotPassword> {
                        val vm = viewModel<CounterViewModel>()
                        remember(Unit) { seen += vm }
                        Text("forgot-password")
                    }
                    flowScopedEntry<AccountDest.Otp> { key ->
                        val vm = viewModel<CounterViewModel>()
                        remember(key) { seen += vm }
                        Text("otp-${key.email}")
                    }
                    flowScopedEntry<AccountDest.ChangePassword> { key ->
                        val vm = viewModel<CounterViewModel>()
                        remember(key) { seen += vm }
                        Text("change-password-${key.isReset}")
                    }
                },
            )
        }
    }

    @Test
    fun allThreeAccountScreens_shareOneViewModelInstance() {
        val backStack = NavBackStack<NavKey>(Dest.Setting, AccountDest.ForgotPassword)
        val seen = mutableListOf<CounterViewModel>()
        setContentWithBackStack(backStack, seen)

        composeTestRule.runOnIdle { backStack.add(AccountDest.Otp("user@example.com")) }
        composeTestRule.onNodeWithText("otp-user@example.com").assertExists()
        composeTestRule.runOnIdle { backStack.add(AccountDest.ChangePassword(isReset = true)) }
        composeTestRule.onNodeWithText("change-password-true").assertExists()

        composeTestRule.runOnIdle {
            assertSame("ForgotPassword and Otp must share one ViewModel", seen[0], seen[1])
            assertSame("Otp and reset ChangePassword must share one ViewModel", seen[1], seen[2])
        }
    }

    @Test
    fun viewModel_isClearedOnlyAfterEveryAccountEntryIsPopped() {
        val backStack = NavBackStack<NavKey>(Dest.Setting, AccountDest.ForgotPassword)
        val seen = mutableListOf<CounterViewModel>()
        setContentWithBackStack(backStack, seen)

        composeTestRule.runOnIdle { backStack.add(AccountDest.Otp("user@example.com")) }
        composeTestRule.onNodeWithText("otp-user@example.com").assertExists()
        val shared = seen[0]

        composeTestRule.runOnIdle { backStack.removeAt(backStack.lastIndex) } // pop Otp
        composeTestRule.onNodeWithText("forgot-password").assertExists()
        composeTestRule.runOnIdle {
            assertTrue(
                "ViewModel must still be alive while ForgotPassword remains on the back stack",
                !shared.cleared,
            )
        }

        composeTestRule.runOnIdle { backStack.removeAt(backStack.lastIndex) } // pop ForgotPassword
        composeTestRule.runOnIdle {
            assertTrue("ViewModel must be cleared once no AccountDest entry remains", shared.cleared)
        }
    }
}
