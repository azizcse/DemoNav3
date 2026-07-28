package com.tecfort.navigationtest.ui.list

import com.tecfort.navigationtest.data.model.Item

data class ListUiState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = true,
)
