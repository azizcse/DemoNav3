package com.tecfort.navigationtest.ui.itemflow

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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ItemFlowViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val items = List(50) { Item(id = it, title = "This is item number $it") }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState starts loading then emits the requested item`() = runTest {
        val viewModel = ItemFlowViewModel(FakeItemRepository(items))
        assertTrue(viewModel.uiState.value.isLoading)

        val collectorJob = launch { viewModel.uiState.collect {} }
        viewModel.loadItem(7)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(items[7], state.item)
        assertTrue(!state.isLoading)

        collectorJob.cancel()
    }

    @Test
    fun `incrementTapCount and onNoteChange mutate uiState`() = runTest {
        val viewModel = ItemFlowViewModel(FakeItemRepository(items))
        val collectorJob = launch { viewModel.uiState.collect {} }
        viewModel.loadItem(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.incrementTapCount()
        viewModel.incrementTapCount()
        viewModel.onNoteChange("hello")

        val state = viewModel.uiState.value
        assertEquals(2, state.tapCount)
        assertEquals("hello", state.note)

        collectorJob.cancel()
    }

    @Test
    fun `loadItem is idempotent for the same item id`() = runTest {
        val viewModel = ItemFlowViewModel(FakeItemRepository(items))
        val collectorJob = launch { viewModel.uiState.collect {} }

        viewModel.loadItem(1)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.incrementTapCount()
        viewModel.onNoteChange("shared note")

        // A second screen entering the same flow scope calls loadItem again with the same id -
        // this must not reset the shared tapCount/note.
        viewModel.loadItem(1)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.tapCount)
        assertEquals("shared note", state.note)

        collectorJob.cancel()
    }

    @Test
    fun `loadItem with a different id replaces the item`() = runTest {
        val viewModel = ItemFlowViewModel(FakeItemRepository(items))
        val collectorJob = launch { viewModel.uiState.collect {} }

        viewModel.loadItem(1)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(items[1], viewModel.uiState.value.item)

        viewModel.loadItem(2)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(items[2], viewModel.uiState.value.item)

        collectorJob.cancel()
    }

    @Test
    fun `loadItem with an unknown id emits a null item`() = runTest {
        val viewModel = ItemFlowViewModel(FakeItemRepository(items))
        val collectorJob = launch { viewModel.uiState.collect {} }

        viewModel.loadItem(999)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.item)
        assertTrue(!state.isLoading)

        collectorJob.cancel()
    }

    private class FakeItemRepository(private val items: List<Item>) : ItemRepository {
        override fun getItems(): Flow<List<Item>> = flowOf(items)
        override fun getItem(id: Int): Flow<Item?> = flowOf(items.find { it.id == id })
    }
}
