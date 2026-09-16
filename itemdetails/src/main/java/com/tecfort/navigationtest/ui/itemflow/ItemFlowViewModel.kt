package com.tecfort.navigationtest.ui.itemflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecfort.navigationtest.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel shared by DetailsScreen and ReviewScreen. One instance exists per item flow scope
 * (see [com.tecfort.navigationtest.navigation.FlowScopedNavKey]), so mutations made on either
 * screen are immediately visible on the other.
 */
@HiltViewModel
class ItemFlowViewModel @Inject constructor(
    private val repository: ItemRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemFlowUiState())
    val uiState: StateFlow<ItemFlowUiState> = _uiState.asStateFlow()

    private var loadedItemId: Int? = null
    private var loadJob: Job? = null

    /**
     * Loads [itemId] once per scope. Idempotent on purpose: both DetailsScreen and ReviewScreen
     * call this, and the second call must not reset [ItemFlowUiState.tapCount] or
     * [ItemFlowUiState.note].
     */
    fun loadItem(itemId: Int) {
        if (loadedItemId == itemId) return
        loadedItemId = itemId
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.getItem(itemId).collect { item ->
                _uiState.update { it.copy(item = item, isLoading = false) }
            }
        }
    }

    fun incrementTapCount() = _uiState.update { it.copy(tapCount = it.tapCount + 1) }

    fun onNoteChange(note: String) = _uiState.update { it.copy(note = note) }
}
