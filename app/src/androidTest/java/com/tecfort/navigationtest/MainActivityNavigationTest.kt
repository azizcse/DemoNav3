package com.tecfort.navigationtest

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// FULL end-to-end instrumented tests: unlike HomeToReviewNavigationTest (which builds a small
// stand-in back stack to avoid Hilt), these launch the REAL MainActivity exactly like a user
// opening the app. This works with real Hilt DI and no extra test setup because androidTest lives
// in the same module as main - the test runs the actual app under test, with its real
// NavigationTestApplication (@HiltAndroidApp) already wired up.
//
// Split into one focused @Test per flow (rather than one giant test doing everything) so that:
//  - each test's name says exactly what it covers,
//  - a failure in one flow (e.g. Details/Review) doesn't hide whether the others still work,
//  - each test runs fast enough to use while iterating on just that flow.
// createAndroidComposeRule relaunches MainActivity fresh for every @Test method, so each test
// starts from splash and drives itself to whatever screen it needs - there's no shared state
// between tests.
@RunWith(AndroidJUnit4::class)
class MainActivityNavigationTest {

    // Launches the real MainActivity (like tapping the app icon) and gives us the same
    // onNodeWithText/performClick tools used in every other Compose UI test in this project.
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // Small pause after each action purely so the transition is visible when watching the device.
    private fun delayAfterAction() = Thread.sleep(ACTION_DELAY_MS)

    // The branded splash screen (SplashScreen.kt, in :app) waits ~1.2s using a plain coroutine
    // delay(), then auto-navigates to Home. Compose's test rule does NOT automatically wait
    // through a plain delay() the way it does for animations, so without this the test would
    // race the splash screen and fail. waitUntil polls the condition until it's true (or times out).
    private fun waitForHomeScreen() {
        composeTestRule.waitUntil(timeoutMillis = SPLASH_TIMEOUT_MS) {
            composeTestRule.onAllNodesWithText("Items").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun launchingApp_landsOnHomeScreen() {
        // App launches on the splash screen, then auto-navigates to Home once it times out.
        waitForHomeScreen()
        composeTestRule.onNodeWithText("Items").assertExists()
    }

    @Test
    fun bottomBar_switchesBetweenAllTabs() {
        waitForHomeScreen()

        // Home -> Favorite: Home's own content should disappear.
        composeTestRule.onNodeWithText("Favorite").performClick()
        delayAfterAction()
        composeTestRule.onNodeWithText("Items").assertDoesNotExist()

        // Favorite -> Setting: Setting's own content should appear.
        composeTestRule.onNodeWithText("Setting").performClick()
        delayAfterAction()
        composeTestRule.onNodeWithText("Edit profile").assertExists()

        // Setting -> Home again.
        composeTestRule.onNodeWithText("Home").performClick()
        delayAfterAction()
        composeTestRule.onNodeWithText("Items").assertExists()
    }

    @Test
    fun settingToProfileSetting_backReturnsToSetting() {
        waitForHomeScreen()
        composeTestRule.onNodeWithText("Setting").performClick()
        delayAfterAction()

        // Push the "Profile Setting" page from Setting.
        composeTestRule.onNodeWithText("Edit profile").performClick()
        delayAfterAction()
        composeTestRule.onNodeWithText("Save and go back").assertExists() // now on Profile Setting

        // System back/gesture should pop it and reveal Setting again.
        Espresso.pressBack()
        delayAfterAction()
        composeTestRule.onNodeWithText("Edit profile").assertExists()
    }

    @Test
    fun homeItemToDetailsToReview_backUnwindsToHome() {
        waitForHomeScreen()

        // Tap the first item on Home to open its Details page.
        composeTestRule.onNodeWithText("This is item number 0").performClick()
        delayAfterAction()
        composeTestRule.onNodeWithText("Open review").assertExists()

        // Details -> Review.
        composeTestRule.onNodeWithText("Open review").performClick()
        delayAfterAction()
        composeTestRule.onNodeWithText("Shared note").assertExists() // Review-only text field

        // Walk all the way back to Home using system back twice (Review -> Details -> Home),
        // proving the whole stack unwinds correctly in both directions.
        Espresso.pressBack()
        delayAfterAction()
        composeTestRule.onNodeWithText("Open review").assertExists() // back on Details

        Espresso.pressBack()
        delayAfterAction()
        composeTestRule.onNodeWithText("Items").assertExists() // back on Home
    }

    private companion object {
        const val ACTION_DELAY_MS = 500L
        const val SPLASH_TIMEOUT_MS = 5_000L
    }
}
