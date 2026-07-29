package com.tecfort.navigationtest.ui.home

import com.tecfort.navigationtest.data.model.Item

data class HomeUiState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = true,
)
