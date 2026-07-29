package com.tecfort.navigationtest.ui.itemflow

import com.tecfort.navigationtest.data.model.Item

/** State shared by DetailsScreen and ReviewScreen for one item flow. */
data class ItemFlowUiState(
    val item: Item? = null,
    val isLoading: Boolean = true,
    /** Bumped from either screen; proves both screens hold the same ViewModel instance. */
    val tapCount: Int = 0,
    /** Free-text note edited on Review and echoed on Details. */
    val note: String = "",
)
