package com.tecfort.navigationtest.navigation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.test.espresso.Espresso
import com.tecfort.navigationtest.data.model.Item
import com.tecfort.navigationtest.ui.details.DetailsScreen
import com.tecfort.navigationtest.ui.home.HomeScreen
import com.tecfort.navigationtest.ui.home.HomeUiState
import com.tecfort.navigationtest.ui.itemflow.ItemFlowUiState
import com.tecfort.navigationtest.ui.review.ReviewScreen
import org.junit.Rule
import org.junit.Test

/**
 * Exercises the real Home -> Details -> Review back-stack wiring from [NavigationRoot] against a
 * real [NavDisplay] - Home's item click adds [Dest.Details], and Details' review click adds
 * [Dest.Review] - using each screen's plain, ViewModel-free overload since the compose rule's
 * host activity is not `@AndroidEntryPoint` (mirrors [FlowScopedViewModelStoreNavEntryDecoratorTest]).
 */
// In short: this test builds a MINI version of NavigationRoot's back stack (just Home, Details,
// Review) instead of launching the whole app. That's needed because the real screens call
// hiltViewModel() to get their ViewModel, and Hilt isn't set up for this test's host Activity.
// So instead we call each screen's other constructor overload, the one that takes a plain
// uiState object directly and has no ViewModel - see e.g. HomeScreen(uiState, onItemClick, ...)
// in HomeScreen.kt. The navigation logic itself (backStack.add(...), onBack, etc.) is the same
// code path the real app uses.
class HomeToReviewNavigationTest {

    // Required by every Compose UI test - lets us call setContent { ... } and interact with
    // whatever gets drawn (onNodeWithText, performClick, ...).
    @get:Rule
    val composeTestRule = createComposeRule()

    // Fake list of 3 items to show on the fake Home screen, same shape as the real repository's data.
    private val items = List(3) { Item(id = it, title = "This is item number $it") }

    // Small pause after an action (click / back press) purely so the transition is visible if
    // you're watching the device screen. Has no effect on whether the test passes or fails.
    private fun delayAfterAction() = Thread.sleep(ACTION_DELAY_MS)

    // Builds the mini back stack described above and puts it on screen. `backStack` is passed in
    // by each test so every test can start on a different screen (e.g. straight on Details).
    private fun setContentWithBackStack(backStack: NavBackStack<NavKey>) {
        composeTestRule.setContent {
            NavDisplay(
                backStack = backStack,
                // Same as NavigationRoot: pressing system back removes the top entry from the
                // stack, which reveals whatever screen is now on top.
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
                // entryProvider maps each back-stack "key" (Dest.Home, Dest.Details, Dest.Review)
                // to the composable screen that should be shown for it - this mirrors the real
                // wiring in NavigationRoot.kt.
                entryProvider = entryProvider {
                    entry<Dest.Home> {
                        HomeScreen(
                            uiState = HomeUiState(items = items, isLoading = false),
                            // Tapping an item pushes a Details entry for that item onto the stack -
                            // this is the real navigation call, same as production code.
                            onItemClick = { item -> backStack.add(Dest.Details(item.id, item.title)) },
                        )
                    }
                    entry<Dest.Details> { key ->
                        DetailsScreen(
                            title = key.title,
                            // Fake state: pretend the item finished "loading" already.
                            uiState = ItemFlowUiState(item = Item(key.itemId, key.title), isLoading = false),
                            onTapClick = {},
                            // Tapping "Open review" pushes a Review entry for the same item.
                            onReviewClick = { backStack.add(Dest.Review(key.itemId)) },
                            onBackClick = { backStack.removeAt(backStack.lastIndex) },
                        )
                    }
                    entry<Dest.Review> { key ->
                        ReviewScreen(
                            uiState = ItemFlowUiState(item = Item(key.itemId, "This is item number ${key.itemId}"), isLoading = false),
                            onTapClick = {},
                            onNoteChange = {},
                            onBackClick = { backStack.removeAt(backStack.lastIndex) },
                        )
                    }
                },
            )
        }
    }

    @Test
    fun clickingHomeItem_navigatesToDetailsScreen() {
        // Start with only Home on the stack.
        val backStack = NavBackStack<NavKey>(Dest.Home)
        setContentWithBackStack(backStack)

        // Tap the second item ("This is item number 1") on the Home list.
        composeTestRule.onNodeWithText("This is item number 1").performClick()
        delayAfterAction()

        // We should now be looking at the Details screen for that item, not Home anymore.
        composeTestRule.onNodeWithText("Item #1").assertExists()
        composeTestRule.onNodeWithText("Open review").assertExists()
    }

    @Test
    fun clickingOpenReviewOnDetails_navigatesToReviewScreen() {
        // Start already on Details for item 2 (Home underneath it on the stack).
        val backStack = NavBackStack<NavKey>(Dest.Home, Dest.Details(2, "This is item number 2"))
        setContentWithBackStack(backStack)

        // Tap the "Open review" button shown on Details.
        composeTestRule.onNodeWithText("Open review").performClick()
        delayAfterAction()

        // We should now be on the Review screen: its own "Item #2" text is showing, and Details'
        // "Open review" button (which only Details has) is gone.
        composeTestRule.onNodeWithText("Item #2").assertExists()
        composeTestRule.onNodeWithText("Open review").assertDoesNotExist()
    }

    @Test
    fun pressingSystemBack_onDetailsScreen_returnsToHomeScreen() {
        // Start on Home, then navigate to Details the same way a user would.
        val backStack = NavBackStack<NavKey>(Dest.Home)
        setContentWithBackStack(backStack)

        composeTestRule.onNodeWithText("This is item number 1").performClick()
        delayAfterAction()
        composeTestRule.onNodeWithText("Item #1").assertExists() // sanity check: we're on Details

        // Simulate the user pressing the phone's system back button/gesture.
        Espresso.pressBack()
        delayAfterAction()

        // Back press should have popped Details off the stack, revealing Home again.
        composeTestRule.onNodeWithText("This is item number 1").assertExists()
        composeTestRule.onNodeWithText("Item #1").assertDoesNotExist()
    }

    private companion object {
        const val ACTION_DELAY_MS = 500L
    }
}
