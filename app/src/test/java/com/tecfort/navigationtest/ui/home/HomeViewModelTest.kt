package com.tecfort.navigationtest.ui.home

import com.tecfort.navigationtest.data.model.Item
import com.tecfort.navigationtest.data.repository.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState starts loading then emits the repository items`() = runTest {
        val fakeItems = List(50) { Item(id = it, title = "This is item number $it") }
        val viewModel = HomeViewModel(FakeItemRepository(fakeItems))

        assertTrue(viewModel.uiState.value.isLoading)

        // uiState is a WhileSubscribed StateFlow: the upstream repository flow only starts
        // being collected once something subscribes to it.
        val collectorJob = launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(50, state.items.size)
        assertEquals(fakeItems, state.items)

        collectorJob.cancel()
    }

    private class FakeItemRepository(private val items: List<Item>) : ItemRepository {
        override fun getItems(): Flow<List<Item>> = flowOf(items)
    }
}
