package com.tecfort.navigationtest.ui.favorite

import com.tecfort.navigationtest.data.model.HealthMetrics

data class FavoriteUiState(
    val isLoading: Boolean = true,
    val isHealthConnectAvailable: Boolean = true,
    val hasPermissions: Boolean = false,
    val requiredPermissions: Set<String> = emptySet(),
    val metrics: HealthMetrics = HealthMetrics(),
)
