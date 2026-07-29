package com.tecfort.navigationtest.ui.details

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.tecfort.navigationtest.data.model.Item
import com.tecfort.navigationtest.ui.itemflow.ItemFlowUiState
import org.junit.Rule
import org.junit.Test

class DetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loading_showsOnlyProgressIndicator() {
        composeTestRule.setContent {
            DetailsScreen(
                title = "Item",
                uiState = ItemFlowUiState(isLoading = true),
                onTapClick = {},
                onReviewClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Shared taps: 0").assertDoesNotExist()
    }

    @Test
    fun loaded_rendersSharedState() {
        composeTestRule.setContent {
            DetailsScreen(
                title = "This is item number 3",
                uiState = ItemFlowUiState(
                    item = Item(id = 3, title = "This is item number 3"),
                    isLoading = false,
                    tapCount = 5,
                    note = "hello",
                ),
                onTapClick = {},
                onReviewClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Item #3").assertExists()
        composeTestRule.onNodeWithText("Shared taps: 5").assertExists()
        composeTestRule.onNodeWithText("Shared note: hello").assertExists()
    }

    @Test
    fun clickingTap_invokesOnTapClick() {
        var tapped = false
        composeTestRule.setContent {
            DetailsScreen(
                title = "Item",
                uiState = ItemFlowUiState(item = Item(id = 1, title = "Item"), isLoading = false),
                onTapClick = { tapped = true },
                onReviewClick = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Tap (shared)").assertHasClickAction()
        composeTestRule.onNodeWithText("Tap (shared)").performClick()

        assert(tapped) { "Expected onTapClick to be invoked" }
    }

    @Test
    fun clickingOpenReview_invokesOnReviewClick() {
        var reviewClicked = false
        composeTestRule.setContent {
            DetailsScreen(
                title = "Item",
                uiState = ItemFlowUiState(item = Item(id = 1, title = "Item"), isLoading = false),
                onTapClick = {},
                onReviewClick = { reviewClicked = true },
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Open review").assertHasClickAction()
        composeTestRule.onNodeWithText("Open review").performClick()

        assert(reviewClicked) { "Expected onReviewClick to be invoked" }
    }
}
