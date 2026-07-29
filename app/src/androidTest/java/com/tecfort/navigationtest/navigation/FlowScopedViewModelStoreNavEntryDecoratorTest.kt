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
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Verifies [FlowScopedViewModelStoreNavEntryDecorator] against a real [NavDisplay], without any
 * Hilt test infra: the compose rule's host activity is not `@AndroidEntryPoint`, so these tests
 * use a plain [ViewModel] obtained with [androidx.lifecycle.viewmodel.compose.viewModel], not
 * `hiltViewModel()`.
 */
class FlowScopedViewModelStoreNavEntryDecoratorTest {

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
                    entry<Dest.Home> { Text("home") }
                    flowScopedEntry<Dest.Details> { key ->
                        val vm = viewModel<CounterViewModel>()
                        remember(key) { seen += vm }
                        Text("details-${key.itemId}")
                    }
                    flowScopedEntry<Dest.Review> { key ->
                        val vm = viewModel<CounterViewModel>()
                        remember(key) { seen += vm }
                        Text("review-${key.itemId}")
                    }
                },
            )
        }
    }

    @Test
    fun detailsAndReview_shareOneViewModelInstance() {
        val backStack = NavBackStack<NavKey>(Dest.Home, Dest.Details(1, "One"))
        val seen = mutableListOf<CounterViewModel>()
        setContentWithBackStack(backStack, seen)

        composeTestRule.runOnIdle { backStack.add(Dest.Review(1)) }
        composeTestRule.onNodeWithText("review-1").assertExists()

        composeTestRule.runOnIdle {
            assertSame("Details and Review for the same item must share one ViewModel", seen[0], seen[1])
        }
    }

    @Test
    fun differentItems_getDifferentViewModelInstances() {
        val backStack = NavBackStack<NavKey>(Dest.Home, Dest.Details(1, "One"))
        val seen = mutableListOf<CounterViewModel>()
        setContentWithBackStack(backStack, seen)

        composeTestRule.runOnIdle {
            backStack.removeAt(backStack.lastIndex) // pop Details(1)
            backStack.add(Dest.Details(2, "Two"))
        }
        composeTestRule.onNodeWithText("details-2").assertExists()

        composeTestRule.runOnIdle {
            assertNotSame("Different items must not share a ViewModel", seen[0], seen[1])
        }
    }

    @Test
    fun viewModel_isClearedOnlyAfterBothEntriesOfTheScopeArePopped() {
        val backStack = NavBackStack<NavKey>(Dest.Home, Dest.Details(1, "One"))
        val seen = mutableListOf<CounterViewModel>()
        setContentWithBackStack(backStack, seen)

        composeTestRule.runOnIdle { backStack.add(Dest.Review(1)) }
        composeTestRule.onNodeWithText("review-1").assertExists()
        val shared = seen[0]

        composeTestRule.runOnIdle { backStack.removeAt(backStack.lastIndex) } // pop Review(1)
        composeTestRule.onNodeWithText("details-1").assertExists()
        composeTestRule.runOnIdle {
            assertTrue(
                "ViewModel must still be alive while Details(1) remains on the back stack",
                !shared.cleared,
            )
        }

        composeTestRule.runOnIdle { backStack.removeAt(backStack.lastIndex) } // pop Details(1)
        composeTestRule.runOnIdle {
            assertTrue("ViewModel must be cleared once no entry of the scope remains", shared.cleared)
        }
    }
}
