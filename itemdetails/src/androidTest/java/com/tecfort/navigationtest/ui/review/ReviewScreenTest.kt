package com.tecfort.navigationtest.ui.review

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.tecfort.navigationtest.data.model.Item
import com.tecfort.navigationtest.ui.itemflow.ItemFlowUiState
import org.junit.Rule
import org.junit.Test

class ReviewScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loading_showsOnlyProgressIndicator() {
        composeTestRule.setContent {
            ReviewScreen(
                uiState = ItemFlowUiState(isLoading = true),
                onTapClick = {},
                onNoteChange = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Shared taps: 0").assertDoesNotExist()
    }

    @Test
    fun loaded_rendersSharedStateFromUiStateOnly() {
        composeTestRule.setContent {
            ReviewScreen(
                uiState = ItemFlowUiState(
                    item = Item(id = 7, title = "This is item number 7"),
                    isLoading = false,
                    tapCount = 2,
                ),
                onTapClick = {},
                onNoteChange = {},
                onBackClick = {},
            )
        }

        // Item id/title are rendered purely from uiState - never passed to ReviewScreen as a
        // separate argument - proving Details and Review share the same ViewModel state.
        composeTestRule.onNodeWithText("Item #7").assertExists()
        composeTestRule.onNodeWithText("This is item number 7").assertExists()
        composeTestRule.onNodeWithText("Shared taps: 2").assertExists()
    }

    @Test
    fun clickingTap_invokesOnTapClick() {
        var tapped = false
        composeTestRule.setContent {
            ReviewScreen(
                uiState = ItemFlowUiState(item = Item(id = 1, title = "Item"), isLoading = false),
                onTapClick = { tapped = true },
                onNoteChange = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Tap (shared)").assertHasClickAction()
        composeTestRule.onNodeWithText("Tap (shared)").performClick()

        assert(tapped) { "Expected onTapClick to be invoked" }
    }

    @Test
    fun typingInNoteField_invokesOnNoteChange() {
        var note: String? = null
        composeTestRule.setContent {
            ReviewScreen(
                uiState = ItemFlowUiState(item = Item(id = 1, title = "Item"), isLoading = false),
                onTapClick = {},
                onNoteChange = { note = it },
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Shared note").performTextInput("hi")

        assert(note == "hi") { "Expected onNoteChange to be invoked with \"hi\" but was $note" }
    }
}
