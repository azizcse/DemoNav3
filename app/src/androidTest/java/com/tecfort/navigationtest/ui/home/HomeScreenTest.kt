package com.tecfort.navigationtest.ui.home

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.tecfort.navigationtest.data.model.Item
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clickingAnItem_invokesOnItemClickWithThatItem() {
        val items = List(3) { Item(id = it, title = "This is item number $it") }
        var clicked: Item? = null

        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(items = items, isLoading = false),
                onItemClick = { clicked = it },
            )
        }

        composeTestRule.onNodeWithText("This is item number 1").assertHasClickAction()
        composeTestRule.onNodeWithText("This is item number 1").performClick()

        assert(clicked == items[1]) { "Expected ${items[1]} but was $clicked" }
    }
}
