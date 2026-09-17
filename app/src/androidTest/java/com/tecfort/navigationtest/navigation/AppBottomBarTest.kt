package com.tecfort.navigationtest.navigation

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

// UI test for the bottom navigation bar (AppBottomBar). It renders ONLY that one composable
// (not the whole app), passes in a fake onTabClick lambda, and checks that lambda gets called
// correctly when a tab is tapped. This is faster and more reliable than testing through the
// whole app because there is nothing else (navigation, ViewModels, etc.) that can go wrong.
class AppBottomBarTest {

    // This JUnit rule is what lets us call setContent { ... } below and interact with
    // composables (onNodeWithText, performClick, ...) in a test. Required for every Compose UI test.
    @get:Rule
    val composeTestRule = createComposeRule()

    // Small pause after a click so that if you're watching the emulator/device screen while the
    // test runs, you can actually see the tab switch happen instead of it flashing by instantly.
    // Not required for the test to pass - purely for visual observation.
    private fun delayAfterAction() = Thread.sleep(ACTION_DELAY_MS)

    @Test
    fun clickingATab_invokesOnTabClickWithThatTab() {
        // Holds whatever tab the bar reports was clicked, so we can check it after the click.
        var clicked: TopLevelDestination? = null

        // Draws just the AppBottomBar composable on screen, with "Home" marked as selected.
        // Whenever a tab is tapped, onTabClick stores it in `clicked`.
        composeTestRule.setContent {
            AppBottomBar(
                selected = TopLevelDestination.HOME,
                onTabClick = { clicked = it },
            )
        }

        // Find the "Favorite" tab by its visible text and confirm it's actually clickable.
        composeTestRule.onNodeWithText("Favorite").assertHasClickAction()
        // Simulate a real tap on it.
        composeTestRule.onNodeWithText("Favorite").performClick()
        delayAfterAction()

        // The tap should have invoked onTabClick with TopLevelDestination.FAVORITE.
        assert(clicked == TopLevelDestination.FAVORITE) { "Expected FAVORITE but was $clicked" }
    }

    @Test
    fun clickingEachTab_invokesOnTabClickWithThatTab() {
        // Records every tab click, in the order they happen.
        val clicks = mutableListOf<TopLevelDestination>()

        composeTestRule.setContent {
            AppBottomBar(
                selected = TopLevelDestination.HOME,
                onTabClick = { clicks += it },
            )
        }

        // Click every tab (Home, Favorite, Setting) one after another by its label text.
        TopLevelDestination.entries.forEach { tab ->
            composeTestRule.onNodeWithText(tab.label).performClick()
            delayAfterAction()
        }

        // We should have recorded one click per tab, in the same order we clicked them.
        assert(clicks == TopLevelDestination.entries.toList()) {
            "Expected a click for every tab in order but was $clicks"
        }
    }

    private companion object {
        const val ACTION_DELAY_MS = 500L
    }
}
